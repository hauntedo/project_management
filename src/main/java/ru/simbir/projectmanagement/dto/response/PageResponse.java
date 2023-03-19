package ru.simbir.projectmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Schema(name = "page response")
public class PageResponse<T> {

    @JsonProperty("content")
    @Schema(name = "content")
    private List<T> content;

    @JsonProperty("total_pages")
    @Schema(name = "total_pages")
    private int totalPages;

    @JsonProperty("total_elements")
    @Schema(name = "total_elements")
    private long totalElements;

}
