package com.team17.poc.box.service;

import com.team17.poc.box.dto.LocationRequestDto;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoxService {

    private final LocationRepository locationRepository;

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
}
