@echo off
chcp 65001 >nul
cls
echo ========================================
echo      API 客戶端生成工具
echo ========================================
echo 1. Swagger Codegen (傳統)
echo 2. OpenAPI Generator (現代，推薦)
echo.

:: 使用 set /p 等待用戶輸入
set /p GENERATOR_CHOICE="請選擇要使用的生成器 (輸入 1 或 2)："

:: 移除可能的多餘空格
set GENERATOR_CHOICE=%GENERATOR_CHOICE: =%

if "%GENERATOR_CHOICE%"=="1" (
    set CLI_JAR=E:\xaingmu\test\javaFx-api\swagger-codegen-cli-3.0.77.jar
    set CLI_TYPE=swagger
    echo 你選擇了：Swagger Codegen
) else if "%GENERATOR_CHOICE%"=="2" (
    set CLI_JAR=D:\Project\Java\HIM\frontend\openapi-generator-cli-7.8.0.jar
    set CLI_TYPE=openapi
    echo 你選擇了：OpenAPI Generator
) else (
    echo ❌ 輸入錯誤：你輸入了 "%GENERATOR_CHOICE%"
    echo 請輸入 1 或 2
    pause
    exit /b 1
)

:: 共用變數
SET SWAGGER_FILE=C:\Users\76569\Downloads\api-docs.json
SET TEMP_OUTPUT=D:\Project\Java\HIM\frontend\temp_generated
SET TARGET_DIR=D:\Project\Java\HIM\frontend\src\main\java

echo.
echo [1/4] 檢查檔案是否存在...

if not exist "%CLI_JAR%" (
    echo ❌ 找不到 CLI jar：%CLI_JAR%
    echo 請確認檔案是否存在，或修改路徑
    pause
    exit /b 1
)

if not exist "%SWAGGER_FILE%" (
    echo ❌ 找不到 Swagger 文件：%SWAGGER_FILE%
    pause
    exit /b 1
)

echo ✅ 所有檔案都存在

echo.
echo [2/4] 開始生成 API 客戶端代碼...
echo - 輸入文件：%SWAGGER_FILE%
echo - 臨時目錄：%TEMP_OUTPUT%
echo - 生成器：%CLI_TYPE%

:: 根據選擇的生成器執行對應命令
if "%CLI_TYPE%"=="swagger" (
    echo 使用 Swagger Codegen 生成...
    java -jar "%CLI_JAR%" generate ^
      -i "%SWAGGER_FILE%" ^
      -l java ^
      -o "%TEMP_OUTPUT%" ^
      --api-package io.swagger.client.api ^
      --model-package io.swagger.client.model ^
      --invoker-package io.swagger.client ^
      --library okhttp-gson
) else (
    echo 使用 OpenAPI Generator 生成...
    java -jar "%CLI_JAR%" generate ^
      -i "%SWAGGER_FILE%" ^
      -g java ^
      -o "%TEMP_OUTPUT%" ^
      --api-package io.swagger.client.api ^
      --model-package io.swagger.client.model ^
      --invoker-package io.swagger.client ^
      --library okhttp-gson ^
      --additional-properties dateLibrary=java8,useJakartaEe=true
)

if %errorlevel% neq 0 (
    echo ❌ 生成失敗！錯誤碼：%errorlevel%
    echo.
    echo 嘗試使用簡化命令重新執行...
    
    if "%CLI_TYPE%"=="swagger" (
        java -jar "%CLI_JAR%" generate ^
          -i "%SWAGGER_FILE%" ^
          -l java ^
          -o "%TEMP_OUTPUT%"
    ) else (
        java -jar "%CLI_JAR%" generate ^
          -i "%SWAGGER_FILE%" ^
          -g java ^
          -o "%TEMP_OUTPUT%"
    )
      
    if %errorlevel% neq 0 (
        echo ❌ 再次失敗，請檢查：
        echo   - Swagger 文件格式是否正確
        echo   - CLI 工具是否完整
        pause
        exit /b %errorlevel%
    )
)

echo ✅ [3/4] 代碼生成成功！

echo.
echo [4/4] 複製到正確位置...

:: 確保目標目錄存在
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

:: 檢查生成的代碼位置
if exist "%TEMP_OUTPUT%\src\main\java\io" (
    echo 找到標準結構，複製中...
    xcopy /E /I /Y "%TEMP_OUTPUT%\src\main\java\io" "%TARGET_DIR%\io"
) else if exist "%TEMP_OUTPUT%\io" (
    echo 找到扁平結構，複製中...
    xcopy /E /I /Y "%TEMP_OUTPUT%\io" "%TARGET_DIR%\io"
) else (
    echo ⚠️ 找不到 io 目錄，顯示臨時目錄內容：
    dir "%TEMP_OUTPUT%"
)

:: 顯示複製結果
echo.
echo 目標目錄內容：
if exist "%TARGET_DIR%\io" (
    dir "%TARGET_DIR%\io\swagger\client" 2>nul
    echo ✅ 代碼已成功複製
) else (
    echo ⚠️ 請手動檢查 %TEMP_OUTPUT%
)

:: 詢問是否保留臨時目錄
echo.
echo 是否要保留臨時目錄供檢查？(Y/N)
set /p KEEP_TEMP="請選擇 (Y/N): "

if /i "%KEEP_TEMP%"=="N" (
    echo 清理臨時文件...
    rmdir /s /q "%TEMP_OUTPUT%" 2>nul
    echo ✅ 臨時目錄已清理
) else (
    echo 臨時目錄保留在：%TEMP_OUTPUT%
)

echo.
echo ========================================
echo ✅ 所有作業完成！
echo ========================================
pause