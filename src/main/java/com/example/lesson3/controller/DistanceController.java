package com.example.lesson3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lesson3.model.Store;
import com.example.lesson3.service.StoreService;
import com.example.lesson3.utils.GeoUtil;

@RestController
@RequestMapping("/api")
public class DistanceController {

	private static final Logger log = LoggerFactory.getLogger(DistanceController.class);

	@Autowired
	private StoreService storeService;

	@PostMapping("/distance")
	public ResponseEntity<Map<String, Object>> calculateDistance(@RequestBody Map<String, Object> request) {
		try {
			Object latObj = request.get("latCurrent");
			Object lonObj = request.get("lonCurrent");
			Object storeIdObj = request.get("storeId");
			if (latObj == null || lonObj == null || storeIdObj == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin đầu vào."));
			}
			double latCurrent = ((Number) latObj).doubleValue();
			double lonCurrent = ((Number) lonObj).doubleValue();
			long storeId = Long.parseLong(storeIdObj.toString());

			Store store = storeService.findById(storeId)
					.orElseThrow(() -> new RuntimeException("Store not found: " + storeId));
			double[] storeLatLng = GeoUtil.getLatLngFromAddress(store.getAddress());
			double distance = GeoUtil.calculateDistance(latCurrent, lonCurrent, storeLatLng[0], storeLatLng[1]);

			return ResponseEntity.ok(Map.of("distance", String.format("%.2f", distance)));

		} catch (Exception e) {
			log.warn("Error calculating distance: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Không thể lấy tọa độ cho địa chỉ cửa hàng."));
		}
	}

	@PostMapping("/list/distances")
	public ResponseEntity<Map<String, Object>> getDistances(@RequestBody Map<String, Object> request) {
		try {
			Object latObj = request.get("latCurrent");
			Object lonObj = request.get("lonCurrent");
			Object storeIdsObj = request.get("storeIds");
			if (latObj == null || lonObj == null || storeIdsObj == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin đầu vào."));
			}
			double latCurrent = ((Number) latObj).doubleValue();
			double lonCurrent = ((Number) lonObj).doubleValue();

			@SuppressWarnings("unchecked")
			List<String> storeIdStrings = (List<String>) storeIdsObj;
			List<Long> storeIds = storeIdStrings.stream()
					.map(Long::parseLong)
					.collect(Collectors.toList());

			Map<String, Double> distances = new HashMap<>();
			for (Long storeId : storeIds) {
				Store store = storeService.findById(storeId).orElse(null);
				if (store != null) {
					try {
						double[] latLng = GeoUtil.getLatLngFromAddress(store.getAddress());
						double distance = GeoUtil.calculateDistance(latCurrent, lonCurrent, latLng[0], latLng[1]);
						distances.put(storeId.toString(), distance);
					} catch (Exception e) {
						log.warn("Error calculating distance for storeId={}: {}", storeId, e.getMessage());
					}
				}
			}

			return ResponseEntity.ok(Map.of("distances", distances));
		} catch (Exception e) {
			log.warn("Error calculating list distances: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Lỗi khi tính khoảng cách."));
		}
	}

}
