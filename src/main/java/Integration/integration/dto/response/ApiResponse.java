package Integration.integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자 자동으로 만들어줌
public class ApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    // 요청 성공
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청에 성공했습니다.", data);
    }

    // 요청 실패
    public static ApiResponse<?> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
