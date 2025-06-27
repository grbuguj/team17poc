package com.team17.poc.box.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.repository.MemberRepository;
import com.team17.poc.box.dto.*;
import com.team17.poc.box.entity.Item;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.repository.ItemRepository;
import com.team17.poc.box.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoxService {

    private final LocationRepository locationRepository;

    private final ItemRepository itemRepository; // 제품 추가 하면서 추가함.

    // 장소 전체 조회
    public List<Location> getLocations(Long memberId) {

        return locationRepository.findByMemberId(memberId);
    }

    public List<LocationWithCountDto> getLocationsWithItemCount(Long memberId) {
        List<Location> locations = locationRepository.findByMemberId(memberId);

        return locations.stream().map(location -> {
            int itemCount = itemRepository.countByLocationId(location.getId());
            return new LocationWithCountDto(
                    location.getId(),
                    location.getName(),
                    location.getDescription(),
                    location.getImagePath(),
                    itemCount
            );
        }).collect(Collectors.toList());
    }



    // 장소 추가
    /*
    public Location addLocation(Long memberId, LocationRequestDto dto) {
        String imagePath = null;

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            try {
                // 로컬에 저장하는 예시 (S3라면 따로 구현)
                String filename = UUID.randomUUID() + "_" + dto.getImage().getOriginalFilename();
                Path path = Paths.get("uploads/" + filename); // 상대 경로 저장
                Files.createDirectories(path.getParent()); // 디렉토리 없으면 생성
                dto.getImage().transferTo(path.toFile());
                imagePath = path.toString(); // 또는 "/uploads/" + filename
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }


        Location location = Location.builder()
                .name(dto.getName())
                .memberId(memberId)
                .imagePath(imagePath)
                .build();
        return locationRepository.save(location);
    }

     */

    // 장소 등록 - 이미지 추가 관련 새로운 추가 사항
    @Transactional
    public Location addLocation(Long memberId, LocationRequestDto dto) {
        MultipartFile image = dto.getImage();
        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String baseDir = System.getProperty("user.dir"); // 프로젝트 루트 경로
            String uploadDir = baseDir + "/uploads/location/";

            try {
                Files.createDirectories(Paths.get(uploadDir)); // 경로 없으면 자동 생성
                image.transferTo(new File(uploadDir + filename)); // 이미지 저장
                imagePath = "/uploads/location/" + filename; // DB에 저장될 경로
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }

        Location location = Location.builder()
                .memberId(memberId)
                .name(dto.getName())
                .description(dto.getDescription())
                .imagePath(imagePath)
                .build();

        return locationRepository.save(location);
    }



    // 장소 수정 (이미지 넣기 전)
    /*
    public Location updateLocation(Long locationId, LocationRequestDto dto) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다"));
        location.setName(dto.getName());
        return locationRepository.save(location);
    }
     */

    // 장소 수정 (new)
    public Location updateLocation(Long locationId, LocationRequestDto dto) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));

        if (dto.getName() != null) {
            location.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            location.setDescription(dto.getDescription());
        }

        MultipartFile image = dto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String baseDir = System.getProperty("user.dir");
            String uploadDir = baseDir + "/uploads/location/";

            try {
                Files.createDirectories(Paths.get(uploadDir));
                image.transferTo(new File(uploadDir + filename));
                String imagePath = "/uploads/location/" + filename;
                location.setImagePath(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }

        return locationRepository.save(location);
    }


    // 장소 삭제
    public void deleteLocation(Long locationId) {
        itemRepository.deleteByLocationId(locationId); // 먼저 관련 아이템 삭제 (새로 추가)
        locationRepository.deleteById(locationId);
    }


    // 2. 제품 추가 기능 구현 (창고)
    /* api 테스트 때문에 잠시 주석처리.
    @Transactional
    public void addItem(Member member, ItemRequestDto dto) {
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 없습니다."));

        Item item = Item.builder()
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .registerDate(dto.getRegisterDate())
                .expireDate(dto.getExpireDate())
                .alarmEnabled(dto.isAlarmEnabled())
                .location(location)
                .member(member)
                .build();

        itemRepository.save(item);
    }
     */

    private final MemberRepository memberRepository;

    @Transactional
    public void addItem(Long memberId, ItemRequestDto dto) {

        if (dto.getLocationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "locationId가 없습니다.");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .filter(loc -> loc.getMemberId().equals(memberId))
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Item item = Item.builder()
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .registerDate(dto.getRegisterDate())
                .expireDate(dto.getExpireDate())
                .alarmEnabled(dto.isAlarmEnabled())
                .location(location)
                .member(member)
                .build();

        itemRepository.save(item);
    }


    // 제품 조회 관련 부분
    public List<BoxResponseDto> getItemsByMemberId(Long memberId) {
        List<Item> items = itemRepository.findByMemberId(memberId);
        return items.stream()
                .map(BoxResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 상세 제품 조회 관련 부분
    public BoxResponseDto getItemByIdAndMember(Long itemId, Long memberId) {
        Item item = itemRepository.findByIdAndMember_Id(itemId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 없습니다."));
        return BoxResponseDto.fromEntity(item);
    }


    // 제품 정렬 기능 (유통기한 만료 순, 최신순, 과거순)
    public List<BoxResponseDto> getSortedItemsByMemberId(Long memberId, String sortBy) {
        List<Item> items;

        switch (sortBy) {
            case "expireDate":
                items = itemRepository.findByMemberIdOrderByExpireDateAsc(memberId);
                break;
            case "latest":
                items = itemRepository.findByMemberIdOrderByRegisterDateDesc(memberId);
                break;
            case "past":
                items = itemRepository.findByMemberIdOrderByRegisterDateAsc(memberId);
                break;
            default:
                items = itemRepository.findByMemberIdOrderByLocationIdAsc(memberId);
                break;
        }

        return items.stream()
                .map(BoxResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 제품 수정 관련
    @Transactional
    public void updateItem(Long itemId, ItemRequestDto dto, Long memberId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 제품이 없습니다."));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

        item.updateFromDto(dto, location, member);
    }

    // 제품 삭제 관련
    @Transactional
    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("해당 제품이 없습니다.");
        }
        itemRepository.deleteById(itemId);
    }




    // 유통기한 추가하며 새롭게 추가된 부분.
    private final Map<Long, TempScanResult> tempScanStorage = new ConcurrentHashMap<>();
    // 임시 저장된 바코드 값을 담은 것. (새로 추가함)

    public void storeTempScan(Long memberId, TempScanResult result) {
        tempScanStorage.put(memberId, result);

        // 바코드 -> 세션 id 역추적용 맵핑 추가함.
        barcodeToSessionIdMap.put(result.getBarcodeId(), String.valueOf(memberId));
        barcodeToMemberIdMap.put(result.getBarcodeId(), memberId);
    }

    public TempScanResult getTempScan(Long memberId) {
        TempScanResult result = tempScanStorage.get(memberId);
        if (result == null) {
            throw new IllegalArgumentException("유효하지 않은 세션 ID입니다.");
        }
        return result;
    }


    // 세션 id 관련 새롭게 추가.

    private final Map<String, String> barcodeToSessionIdMap = new HashMap<>();
    private final Map<String, Long> barcodeToMemberIdMap = new HashMap<>();

    public Optional<String> findSessionIdByBarcode(String barcode) {
        // barcode → sessionId 맵핑 저장돼 있다고 가정
        return Optional.ofNullable(barcodeToSessionIdMap.get(barcode));
    }


    public Optional<Long> findMemberIdByBarcode(String barcode) {
        // barcode → sessionId 맵핑 저장돼 있다고 가정
        return Optional.ofNullable(barcodeToMemberIdMap.get(barcode));
    }



    /* 알람 설정 관련 */

    // ✅ 사용자별 기준일 저장용 (메모리 기반)
    private final Map<Long, Integer> userNotifyMap = new HashMap<>();

    private final int DEFAULT_NOTIFY_DAYS = 14;

    /**
     * ✅ 기준일 저장 (사용자별 설정 값 저장)
     */
    public void setNotifyStandard(Long memberId, int standardDays) {
        userNotifyMap.put(memberId, standardDays);
    }

    /**
     * ✅ 기준일 이내 유통기한 도래 상품 조회
     */
    public List<NotifyItemDto> getItemsExpiringSoon(Long memberId, Integer requestStandardDays) {
        int standardDays = Optional.ofNullable(requestStandardDays)
                .orElse(userNotifyMap.getOrDefault(memberId, DEFAULT_NOTIFY_DAYS));

        LocalDate threshold = LocalDate.now().plusDays(standardDays);

        List<Item> items = itemRepository.findItemsByMemberIdAndExpireDateLessThanEqual(memberId, threshold);

        return items.stream()
                .map(NotifyItemDto::from)
                .toList();
    }

}
