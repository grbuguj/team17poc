package com.team17.poc.box.controller;

import com.team17.poc.box.dto.LocationRequestDto;
import com.team17.poc.box.entity.Location;
import com.team17.poc.box.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/box/locations")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    // ✅ 장소 목록 조회
    @GetMapping
    public List<Location> getLocations(@RequestParam Long memberId) {
        return boxService.getLocations(memberId);
    }

    // ✅ 장소 등록
    @PostMapping
    public Location addLocation(@RequestParam Long memberId, @RequestBody LocationRequestDto dto) {
        return boxService.addLocation(memberId, dto);
    }

    // ✅ 장소 수정
    @PatchMapping("/{locationId}")
    public Location updateLocation(@PathVariable Long locationId, @RequestBody LocationRequestDto dto) {
        return boxService.updateLocation(locationId, dto);
    }

    // ✅ 장소 삭제
    @DeleteMapping("/{locationId}")
    public void deleteLocation(@PathVariable Long locationId) {
        boxService.deleteLocation(locationId);
    }
}
