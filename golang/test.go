package main

import (
	"fmt"
	"os"
	"strings"
	"time"

	nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
)

func main() {
	// 配置
	baseURL := os.Getenv("NANMANAGER_BASE_URL")
	token := os.Getenv("NANMANAGER_TOKEN")

	if baseURL == "" {
		baseURL = "https://your-api-server.com"
	}
	if token == "" {
		token = "your-api-token-here"
	}

	// 创建客户端
	client := nanmanager.NewNanCityManager(baseURL, token)

	fmt.Println("=== NewNanManager API 客户端测试 ===")
	fmt.Printf("Base URL: %s\n", baseURL)
	fmt.Printf("Token: %s\n", token)
	fmt.Println()

	// 测试报告
	testReport := &TestReport{
		StartTime: time.Now(),
		Tests:     make([]TestResult, 0),
	}

	// 1. 测试玩家管理功能
	fmt.Println("=== 1. 测试玩家管理功能 ===")
	testPlayerManagement(client, testReport)

	// 2. 测试服务器管理功能
	fmt.Println("\n=== 2. 测试服务器管理功能 ===")
	testServerManagement(client, testReport)

	// 3. 测试城镇管理功能
	fmt.Println("\n=== 3. 测试城镇管理功能 ===")
	testTownManagement(client, testReport)

	// 4. 测试Token管理功能
	fmt.Println("\n=== 4. 测试Token管理功能 ===")
	testTokenManagement(client, testReport)

	// 5. 测试监控功能
	fmt.Println("\n=== 5. 测试监控功能 ===")
	testMonitoringFeatures(client, testReport)

	// 生成测试报告
	testReport.EndTime = time.Now()
	generateTestReport(testReport)
}

type TestReport struct {
	StartTime time.Time
	EndTime   time.Time
	Tests     []TestResult
}

type TestResult struct {
	Name        string
	Success     bool
	Error       error
	Duration    time.Duration
	Description string
}

func addTestResult(report *TestReport, name, description string, success bool, err error, duration time.Duration) {
	report.Tests = append(report.Tests, TestResult{
		Name:        name,
		Success:     success,
		Error:       err,
		Duration:    duration,
		Description: description,
	})
}

func testPlayerManagement(client *nanmanager.NanCityManagerClient, report *TestReport) {
	var createdPlayerID int32

	// 1.1 获取玩家列表
	start := time.Now()
	players, err := client.ListPlayers(nil, nil, nil, nil, nil)
	duration := time.Since(start)
	success := err == nil
	addTestResult(report, "ListPlayers", "获取玩家列表", success, err, duration)
	if success {
		fmt.Printf("✓ 获取玩家列表成功，共 %d 个玩家\n", players.Total)
	} else {
		fmt.Printf("✗ 获取玩家列表失败: %v\n", err)
	}

	// 1.2 创建玩家
	start = time.Now()
	testPlayerName := fmt.Sprintf("TestPlayer_%d", time.Now().Unix())
	inQQGroup := true
	inQQGuild := false
	inDiscord := false
	createReq := nanmanager.CreatePlayerRequest{
		Name:      testPlayerName,
		InQQGroup: &inQQGroup,
		InQQGuild: &inQQGuild,
		InDiscord: &inDiscord,
	}
	createdPlayer, err := client.CreatePlayer(createReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "CreatePlayer", "创建玩家", success, err, duration)
	if success {
		createdPlayerID = createdPlayer.ID
		fmt.Printf("✓ 创建玩家成功，ID: %d, 名称: %s\n", createdPlayer.ID, createdPlayer.Name)
	} else {
		fmt.Printf("✗ 创建玩家失败: %v\n", err)
		return // 如果创建失败，后续测试无法进行
	}

	// 1.3 获取玩家详情
	start = time.Now()
	player, err := client.GetPlayer(createdPlayerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "GetPlayer", "获取玩家详情", success, err, duration)
	if success {
		fmt.Printf("✓ 获取玩家详情成功，名称: %s, 创建时间: %s\n", player.Name, player.CreatedAt.Format("2006-01-02 15:04:05"))
	} else {
		fmt.Printf("✗ 获取玩家详情失败: %v\n", err)
	}

	// 1.4 更新玩家信息
	start = time.Now()
	newName := testPlayerName + "_Updated"
	updateReq := nanmanager.UpdatePlayerRequest{
		Name: &newName,
	}
	updatedPlayer, err := client.UpdatePlayer(createdPlayerID, updateReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "UpdatePlayer", "更新玩家信息", success, err, duration)
	if success {
		fmt.Printf("✓ 更新玩家信息成功，新名称: %s\n", updatedPlayer.Name)
	} else {
		fmt.Printf("✗ 更新玩家信息失败: %v\n", err)
	}

	// 1.5 封禁玩家
	start = time.Now()
	banReq := nanmanager.BanPlayerRequest{
		BanMode: nanmanager.BanModeTemporary,
		Reason:  "测试封禁",
	}
	duration60 := int64(60)
	banReq.DurationSeconds = &duration60
	err = client.BanPlayer(createdPlayerID, banReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "BanPlayer", "封禁玩家", success, err, duration)
	if success {
		fmt.Printf("✓ 封禁玩家成功\n")
	} else {
		fmt.Printf("✗ 封禁玩家失败: %v\n", err)
	}

	// 1.6 解封玩家
	start = time.Now()
	err = client.UnbanPlayer(createdPlayerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "UnbanPlayer", "解封玩家", success, err, duration)
	if success {
		fmt.Printf("✓ 解封玩家成功\n")
	} else {
		fmt.Printf("✗ 解封玩家失败: %v\n", err)
	}

	// 1.7 验证登录
	start = time.Now()
	serverID := int32(1) // 使用默认服务器ID
	validateReq := nanmanager.ValidateLoginRequest{
		PlayerName: &newName,
		ServerID:   &serverID,
	}
	validateResult, err := client.ValidateLogin(validateReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "ValidateLogin", "验证玩家登录", success, err, duration)
	if success {
		fmt.Printf("✓ 验证登录成功，允许登录: %t\n", validateResult.Allowed)
	} else {
		fmt.Printf("✗ 验证登录失败: %v\n", err)
	}

	// 1.8 删除玩家
	start = time.Now()
	err = client.DeletePlayer(createdPlayerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "DeletePlayer", "删除玩家", success, err, duration)
	if success {
		fmt.Printf("✓ 删除玩家成功\n")
	} else {
		fmt.Printf("✗ 删除玩家失败: %v\n", err)
	}
}

func testServerManagement(client *nanmanager.NanCityManagerClient, report *TestReport) {
	var createdServerID int32

	// 2.1 获取服务器列表
	start := time.Now()
	servers, err := client.ListServers(nil, nil, nil, nil)
	duration := time.Since(start)
	success := err == nil
	addTestResult(report, "ListServers", "获取服务器列表", success, err, duration)
	if success {
		fmt.Printf("✓ 获取服务器列表成功，共 %d 个服务器\n", servers.Total)
	} else {
		fmt.Printf("✗ 获取服务器列表失败: %v\n", err)
	}

	// 2.2 注册服务器
	start = time.Now()
	testServerName := fmt.Sprintf("TestServer_%d", time.Now().Unix())
	testServerAddress := fmt.Sprintf("test-%d.example.com:25565", time.Now().Unix())
	serverType := nanmanager.ServerTypeMinecraft
	registerReq := nanmanager.RegisterServerRequest{
		Name:       testServerName,
		Address:    testServerAddress,
		ServerType: &serverType,
	}
	createdServer, err := client.RegisterServer(registerReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "RegisterServer", "注册服务器", success, err, duration)
	if success {
		createdServerID = createdServer.ID
		fmt.Printf("✓ 注册服务器成功，ID: %d, 名称: %s\n", createdServer.ID, createdServer.Name)
	} else {
		fmt.Printf("✗ 注册服务器失败: %v\n", err)
		return
	}

	// 2.3 获取服务器信息
	start = time.Now()
	server, err := client.GetServer(createdServerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "GetServer", "获取服务器信息", success, err, duration)
	if success {
		fmt.Printf("✓ 获取服务器信息成功，名称: %s, 地址: %s\n", server.Name, server.Address)
	} else {
		fmt.Printf("✗ 获取服务器信息失败: %v\n", err)
	}

	// 2.4 更新服务器信息
	start = time.Now()
	newServerName := testServerName + "_Updated"
	newAddress := fmt.Sprintf("updated-%d.example.com:25565", time.Now().Unix())
	updateReq := nanmanager.UpdateServerRequest{
		Name:    &newServerName,
		Address: &newAddress,
	}
	updatedServer, err := client.UpdateServer(createdServerID, updateReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "UpdateServer", "更新服务器信息", success, err, duration)
	if success {
		fmt.Printf("✓ 更新服务器信息成功，新名称: %s\n", updatedServer.Name)
	} else {
		fmt.Printf("✗ 更新服务器信息失败: %v\n", err)
	}

	// 2.5 获取服务器详细信息
	start = time.Now()
	serverDetail, err := client.GetServerDetail(createdServerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "GetServerDetail", "获取服务器详细信息", success, err, duration)
	if success {
		fmt.Printf("✓ 获取服务器详细信息成功，服务器: %s\n", serverDetail.Server.Name)
		if serverDetail.Status != nil {
			fmt.Printf("  状态: 在线=%t, 当前玩家=%d\n", serverDetail.Status.Online, serverDetail.Status.CurrentPlayers)
		}
	} else {
		fmt.Printf("✗ 获取服务器详细信息失败: %v\n", err)
	}

	// 2.6 删除服务器
	start = time.Now()
	err = client.DeleteServer(createdServerID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "DeleteServer", "删除服务器", success, err, duration)
	if success {
		fmt.Printf("✓ 删除服务器成功\n")
	} else {
		fmt.Printf("✗ 删除服务器失败: %v\n", err)
	}
}

func testTownManagement(client *nanmanager.NanCityManagerClient, report *TestReport) {
	var createdTownID int32

	// 3.1 获取城镇列表
	start := time.Now()
	towns, err := client.ListTowns(nil, nil, nil, nil, nil)
	duration := time.Since(start)
	success := err == nil
	addTestResult(report, "ListTowns", "获取城镇列表", success, err, duration)
	if success {
		fmt.Printf("✓ 获取城镇列表成功，共 %d 个城镇\n", towns.Total)
	} else {
		fmt.Printf("✗ 获取城镇列表失败: %v\n", err)
	}

	// 3.2 创建城镇
	start = time.Now()
	testTownName := fmt.Sprintf("TestTown_%d", time.Now().Unix())
	level := int32(1)
	createReq := nanmanager.CreateTownRequest{
		Name:  testTownName,
		Level: &level,
	}
	createdTown, err := client.CreateTown(createReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "CreateTown", "创建城镇", success, err, duration)
	if success {
		createdTownID = createdTown.ID
		fmt.Printf("✓ 创建城镇成功，ID: %d, 名称: %s\n", createdTown.ID, createdTown.Name)
	} else {
		fmt.Printf("✗ 创建城镇失败: %v\n", err)
		return
	}

	// 3.3 获取城镇详情
	start = time.Now()
	town, err := client.GetTown(createdTownID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "GetTown", "获取城镇详情", success, err, duration)
	if success {
		fmt.Printf("✓ 获取城镇详情成功，名称: %s, 等级: %d\n", town.Name, town.Level)
	} else {
		fmt.Printf("✗ 获取城镇详情失败: %v\n", err)
	}

	// 3.4 更新城镇信息
	start = time.Now()
	newTownName := testTownName + "_Updated"
	newLevel := int32(2)
	updateReq := nanmanager.UpdateTownRequest{
		Name:  &newTownName,
		Level: &newLevel,
	}
	updatedTown, err := client.UpdateTown(createdTownID, updateReq)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "UpdateTown", "更新城镇信息", success, err, duration)
	if success {
		fmt.Printf("✓ 更新城镇信息成功，新名称: %s, 新等级: %d\n", updatedTown.Name, updatedTown.Level)
	} else {
		fmt.Printf("✗ 更新城镇信息失败: %v\n", err)
	}

	// 3.5 获取城镇成员列表
	start = time.Now()
	members, err := client.GetTownMembers(createdTownID, nil, nil)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "GetTownMembers", "获取城镇成员列表", success, err, duration)
	if success {
		fmt.Printf("✓ 获取城镇成员列表成功，共 %d 个成员\n", members.Total)
	} else {
		fmt.Printf("✗ 获取城镇成员列表失败: %v\n", err)
	}

	// 3.6 删除城镇
	start = time.Now()
	err = client.DeleteTown(createdTownID)
	duration = time.Since(start)
	success = err == nil
	addTestResult(report, "DeleteTown", "删除城镇", success, err, duration)
	if success {
		fmt.Printf("✓ 删除城镇成功\n")
	} else {
		fmt.Printf("✗ 删除城镇失败: %v\n", err)
	}
}

func testTokenManagement(client *nanmanager.NanCityManagerClient, report *TestReport) {
	// 4.1 获取Token列表
	start := time.Now()
	tokens, err := client.ListApiTokens()
	duration := time.Since(start)
	success := err == nil
	addTestResult(report, "ListApiTokens", "获取Token列表", success, err, duration)
	if success {
		fmt.Printf("✓ 获取Token列表成功，共 %d 个Token\n", len(tokens.Tokens))
	} else {
		fmt.Printf("✗ 获取Token列表失败: %v\n", err)
	}

	// 注意：Token管理功能可能需要更高权限，这里只测试读取操作
}

func testMonitoringFeatures(client *nanmanager.NanCityManagerClient, report *TestReport) {
	// 5.1 测试心跳功能（需要有效的服务器ID）
	// 由于我们没有真实的服务器ID，这里只是演示API调用
	fmt.Printf("⚠ 监控功能测试需要有效的服务器ID，跳过详细测试\n")

	// 可以尝试获取一个不存在的服务器状态来测试API
	start := time.Now()
	_, err := client.GetServerStatus(999999)
	duration := time.Since(start)
	success := err != nil // 这里期望失败，因为服务器不存在
	addTestResult(report, "GetServerStatus", "获取服务器状态（测试错误处理）", success, err, duration)
	if success {
		fmt.Printf("✓ 错误处理测试成功，正确返回了错误: %v\n", err)
	} else {
		fmt.Printf("✗ 错误处理测试失败，应该返回错误但没有\n")
	}
}

func generateTestReport(report *TestReport) {
	fmt.Println("\n" + strings.Repeat("=", 60))
	fmt.Println("                    测试报告")
	fmt.Println(strings.Repeat("=", 60))
	fmt.Printf("开始时间: %s\n", report.StartTime.Format("2006-01-02 15:04:05"))
	fmt.Printf("结束时间: %s\n", report.EndTime.Format("2006-01-02 15:04:05"))
	fmt.Printf("总耗时: %s\n", report.EndTime.Sub(report.StartTime))
	fmt.Printf("总测试数: %d\n", len(report.Tests))

	successCount := 0
	failCount := 0
	totalDuration := time.Duration(0)

	fmt.Println("\n详细结果:")
	fmt.Println(strings.Repeat("-", 60))
	for i, test := range report.Tests {
		status := "✗ 失败"
		if test.Success {
			status = "✓ 成功"
			successCount++
		} else {
			failCount++
		}
		totalDuration += test.Duration

		fmt.Printf("%2d. %-20s %s (%s)\n", i+1, test.Name, status, test.Duration)
		fmt.Printf("    %s\n", test.Description)
		if test.Error != nil {
			fmt.Printf("    错误: %v\n", test.Error)
		}
		fmt.Println()
	}

	fmt.Println(strings.Repeat("-", 60))
	fmt.Printf("成功: %d, 失败: %d, 成功率: %.1f%%\n",
		successCount, failCount, float64(successCount)/float64(len(report.Tests))*100)
	fmt.Printf("平均耗时: %s\n", totalDuration/time.Duration(len(report.Tests)))
	fmt.Println(strings.Repeat("=", 60))

	// 总结
	if failCount == 0 {
		fmt.Println("🎉 所有测试都通过了！API客户端工作正常。")
	} else if successCount > failCount {
		fmt.Printf("⚠ 大部分测试通过，但有 %d 个测试失败，请检查相关功能。\n", failCount)
	} else {
		fmt.Printf("❌ 测试失败较多（%d个），请检查API连接和权限配置。\n", failCount)
	}
}
