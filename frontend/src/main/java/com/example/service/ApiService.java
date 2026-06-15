package com.example.service;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import javafx.concurrent.Task;
import javafx.application.Platform;
import com.example.config.ApiClientConfig;
import com.example.exception.ApiServiceException;
import com.example.util.DialogManager;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * API 服務層 - 統一的 API 調用入口（單例模式）
 * 
 * 使用方式：
 * // 1. 獲取 API 實例
 * DefaultApi api = ApiService.getInstance().getApi(DefaultApi.class);
 * 
 * // 2. 異步調用
 * ApiService.getInstance().callAsync(
 * () -> api.login(request),
 * this::handleSuccess,
 * this::handleError,
 * this::setLoading
 * );
 */
public class ApiService {

    // ==================== 單例實現 ====================
    private static volatile ApiService instance;

    // 是否自動顯示對話框（可配置）
    private boolean autoShowDialog = true;

    private final ApiClient apiClient;
    private final String initError;
    private final boolean initialized;

    /**
     * 私有構造函數
     */
    private ApiService() {
        String error = null;
        ApiClient client = null;
        boolean ok = false;

        try {
            client = ApiClientConfig.getConfiguredClient();
            ok = true;
        } catch (Exception e) {
            error = "初始化 API 服務失敗：" + e.getMessage();
            e.printStackTrace();
        }

        this.apiClient = client;
        this.initialized = ok;
        this.initError = error;
    }

    /**
     * 獲取單例實例（線程安全）
     */
    public static ApiService getInstance() {
        if (instance == null) {
            synchronized (ApiService.class) {
                if (instance == null) {
                    instance = new ApiService();
                }
            }
        }
        return instance;
    }

    // ==================== 核心 API ====================

    /**
     * 獲取 API 實例
     */
    public <T> T getApi(Class<T> apiClass) {
        checkInitialized();
        try {
            return apiClass.getConstructor(ApiClient.class).newInstance(apiClient);
        } catch (Exception e) {
            throw new ApiServiceException("創建 API 實例失敗：" + apiClass.getSimpleName(), e);
        }
    }

    /**
     * 創建異步任務
     */
    public <R> Task<R> createTask(ApiCallable<R> callable) {
        checkInitialized();
        return new Task<>() {
            @Override
            protected R call() throws Exception {
                return callable.call();
            }
        };
    }

    /**
     * 執行異步調用
     */
    public <R> void callAsync(ApiCallable<R> callable,
            ApiSuccessCallback<R> onSuccess,
            ApiErrorCallback onError,
            LoadingCallback onLoading) {
        checkInitialized();

        Platform.runLater(() -> onLoading.onLoading(true));

        Task<R> task = new Task<>() {
            @Override
            protected R call() throws Exception {
                return callable.call();
            }
        };

        task.setOnSucceeded(e -> {
            try {
                R result = task.getValue();

                // 自動顯示對話框
                // if (autoShowDialog) {
                // showResultDialogIfNeeded(result);
                // }

                Platform.runLater(() -> {
                    try {
                        onSuccess.onSuccess(result);
                    } catch (Exception ex) {
                        handleCallbackError(onError, ex);
                    } finally {
                        onLoading.onLoading(false);
                    }
                });

            } catch (Exception ex) {
                handleTaskError(onError, onLoading, ex);
            }
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            Throwable wrappedError = wrapError(error);

            // 錯誤時顯示對話框
            if (autoShowDialog) {
                showErrorDialogIfNeeded(wrappedError);
            }

            Platform.runLater(() -> {
                onError.onError(wrappedError);
                onLoading.onLoading(false);
            });
        });

        new Thread(task).start();
    }

    /**
     * 執行異步調用- 不需要載入狀態
     */
    public <R> void callAsync(ApiCallable<R> callable,
            ApiSuccessCallback<R> onSuccess,
            ApiErrorCallback onError) {
        callAsync(callable, onSuccess, onError, loading -> {
        });
    }

    /**
     * 執行異步調用- 只關心成功
     */
    public <R> void callAsync(ApiCallable<R> callable,
            ApiSuccessCallback<R> onSuccess) {
        callAsync(callable, onSuccess,
                error -> System.err.println("API調用失敗：" + error.getMessage()),
                loading -> {
                });
    }

    /**
     * 同步調用
     */
    public <R> R callSync(ApiCallable<R> callable) throws ApiServiceException {
        checkInitialized();
        try {
            return callable.call();
        } catch (Exception e) {
            Throwable wrapped = wrapError(e);
            throw new ApiServiceException(wrapped.getMessage(), wrapped);
        }
    }

    // ==================== 對話框處理 ====================

    /**
     * 自動顯示結果對話框
     */
    private void showResultDialogIfNeeded(Object result) {
        if (result == null)
            return;

        Platform.runLater(() -> {
            try {
                DialogManager.getInstance().showResultDialog(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 自動顯示錯誤對話框（失敗時）
     */
    private void showErrorDialogIfNeeded(Throwable error) {
        Platform.runLater(() -> {
            try {
                if (error instanceof ApiServiceException) {
                    ApiServiceException apiEx = (ApiServiceException) error;
                    DialogManager.getInstance().showError(
                            apiEx.getHttpCode() != null ? apiEx.getHttpCode() : 500,
                            error.getMessage());
                } else {
                    DialogManager.getInstance().showError(500, error.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== 私有方法 ====================

    /**
     * 檢查是否初始化成功
     */
    private void checkInitialized() {
        if (!initialized) {
            throw new ApiServiceException(initError != null ? initError : "API 服務未初始化");
        }
    }

    /**
     * 處理任務錯誤
     */
    private <R> void handleTaskError(ApiErrorCallback onError,
            LoadingCallback onLoading,
            Exception ex) {
        Throwable wrapped = wrapError(ex);
        Platform.runLater(() -> {
            onError.onError(wrapped);
            onLoading.onLoading(false);
        });
    }

    /**
     * 處理回調錯誤
     */
    private void handleCallbackError(ApiErrorCallback onError, Exception ex) {
        Platform.runLater(() -> {
            onError.onError(wrapError(ex));
        });
    }

    /**
     * 錯誤包裝
     */
    private Throwable wrapError(Throwable error) {
        if (error instanceof ApiException) {
            ApiException apiEx = (ApiException) error;
            String responseBody = apiEx.getResponseBody();
            if (responseBody != null && !responseBody.isEmpty()) {
                return new ApiServiceException(responseBody, apiEx.getCode(), apiEx);
            }
            return new ApiServiceException("API 調用失敗 (HTTP " + apiEx.getCode() + ")",
                    apiEx.getCode(), apiEx);
        }

        if (error instanceof ConnectException) {
            return new ApiServiceException("無法連接到伺服器，請檢查網路連線", error);
        }

        if (error instanceof SocketTimeoutException) {
            return new ApiServiceException("連接超時，請稍後再試", error);
        }

        if (error instanceof IllegalArgumentException) {
            return new ApiServiceException("參數錯誤：" + error.getMessage(), error);
        }

        return new ApiServiceException("系統錯誤：" + error.getMessage(), error);
    }

    // ==================== 配置方法 ====================

    public void setAutoShowDialog(boolean autoShow) {
        this.autoShowDialog = autoShow;
    }

    public boolean isAutoShowDialog() {
        return autoShowDialog;
    }

    // ==================== 函數式接口 ====================

    @FunctionalInterface
    public interface ApiCallable<R> {
        R call() throws Exception;
    }

    @FunctionalInterface
    public interface ApiSuccessCallback<R> {
        void onSuccess(R result);
    }

    @FunctionalInterface
    public interface ApiErrorCallback {
        void onError(Throwable error);
    }

    @FunctionalInterface
    public interface LoadingCallback {
        void onLoading(boolean isLoading);
    }

    public void callAsync(Object callable, Object onSuccess, ApiErrorCallback onError, LoadingCallback onLoading) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'callAsync'");
    }

    public void callAsync(Object callable, Object onSuccess) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'callAsync'");
    }
}