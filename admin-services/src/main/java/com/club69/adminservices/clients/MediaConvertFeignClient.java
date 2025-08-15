package com.club69.adminservices.clients;

import com.club69.commons.dto.MediaConversionRequest;
import com.club69.commons.dto.MediaInformationRequest;
import com.club69.commons.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("media-convert")
public interface MediaConvertFeignClient {

    @PostMapping("/mediaConversion/convert")
    ResponseEntity<ApiResponse> convert(@RequestBody MediaConversionRequest mediaConversionRequest);

    @PostMapping("/mediaInformation/")
    ResponseEntity<ApiResponse> mediaInfo(@RequestBody MediaInformationRequest mediaInformationRequest);
}
