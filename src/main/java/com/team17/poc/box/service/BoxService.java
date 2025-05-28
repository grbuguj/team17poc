package com.team17.poc.box.service;

import com.team17.poc.auth.entity.Member;
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
        locationRepository.deleteById(locationId);
    }


    // 2. 제품 추가 기능 구현 (창고)
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


    // 유통기한 추가하며 새롭게 추가된 부분.
    private final Map<String, TempScanResult> tempScanStorage = new ConcurrentHashMap<>();
    // 임시 저장된 바코드 값을 담은 것. (새로 추가함)

    public void storeTempScan(String sessionId, TempScanResult result) {
        tempScanStorage.put(sessionId, result);

        // 바코드 -> 세션 id 역추적용 맵핑 추가함.
        barcodeToSessionIdMap.put(result.getBarcodeId(), sessionId);
    }

    public TempScanResult getTempScan(String sessionId) {
        TempScanResult result = tempScanStorage.get(sessionId);
        if (result == null) {
            throw new IllegalArgumentException("유효하지 않은 세션 ID입니다.");
        }
        return result;
    }


    // 세션 id 관련 새롭게 추가.

    private final Map<String, String> barcodeToSessionIdMap = new HashMap<>();


    public Optional<String> findSessionIdByBarcode(String barcode) {
        // barcode → sessionId 맵핑 저장돼 있다고 가정
        return Optional.ofNullable(barcodeToSessionIdMap.get(barcode));
    }


}
