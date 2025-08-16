package com.example.DIVE2025.domain.rescued.dto;

import lombok.Data;
import java.util.List;

@Data
public class RescuedApiResponse {

    private Response response;

    @Data
    public static class Response {
        private Header header;
        private Body body;
    }

    @Data
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    public static class Body {
        private int pageNo;
        private int numOfRows;
        private int totalCount;
        private Items items;
    }

    @Data
    public static class Items {
        private List<RescuedApiItemDto> item;
    }
}
