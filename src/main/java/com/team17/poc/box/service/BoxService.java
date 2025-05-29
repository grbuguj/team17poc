package com.team17.poc.box.service;

import com.team17.poc.auth.entity.Member;
import com.team17.poc.auth.repository.MemberRepository;
import com.team17.poc.box.dto.BoxResponseDto;
import com.team17.poc.box.dto.ItemRequestDto;
import com.team17.poc.box.dto.LocationRequestDto;
import com.team17.poc.box.dto.TempScanResult;
import com.team17.poc.box.entity.Item;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.repository.ItemRepository;
import com.team17.poc.box.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    // 장소 추가
    public Location addLocation(Long memberId, LocationRequestDto dto) {
        Location location = Location.builder()
                .name(dto.getName())
                .memberId(memberId)
                .build();
        return locationRepository.save(location);
    }

    // 장소 수정
    public Location updateLocation(Long locationId, LocationRequestDto dto) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다"));
        location.setName(dto.getName());
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
        Location location = locationRepository.findById(dto.getLocationId())
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

}
