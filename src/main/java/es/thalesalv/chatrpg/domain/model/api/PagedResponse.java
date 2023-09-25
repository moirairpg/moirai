package es.thalesalv.chatrpg.domain.model.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagedResponse<T> {

    private ApiErrorResponse error;
    private Integer numberOfItemsInPage;
    private Integer numberOfPages;
    private Integer currentPage;
    private Integer totalNumberOfItems;
    private List<T> data;
}
