# Go 调用示例

客户端由调用方按 [README](../../golang/README.md) 构造。请求类型位于 `modules` 包，服务入口为 `client.Players/Servers/IPs` 等；旧文档中的顶层快捷方法没有对应实现。以下示例的写入和验证函数只应在指定服务和预期数据范围内调用，本轮未执行真实服务请求。

## 创建、更新与验证

```go
package examples

import (
	nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/modules"
)

func CreateAndValidatePlayer(client *nanmanager.NanCityManagerClient, name, ip string, serverID int32) (*modules.ValidateResponse, error) {
	player, err := client.Players.CreatePlayer(modules.CreatePlayerRequest{
		Name: name, InQQGroup: true,
	})
	if err != nil {
		return nil, err
	}
	inDiscord := false
	if _, err := client.Players.UpdatePlayer(player.ID, modules.UpdatePlayerRequest{InDiscord: &inDiscord}); err != nil {
		return nil, err
	}
	return client.Players.Validate(modules.ValidateRequest{
		Players: []modules.PlayerValidateInfo{{PlayerName: player.Name, IP: ip}},
		ServerID: serverID,
		Login: true,
	})
}
```

更新字段为指针；nil 表示不发送，指向 false 或空字符串表示显式更新。检查每个验证结果的 Allowed 和 Reason；返回成功不表示批次中全部玩家都被允许。

## 临时封禁

```go
package examples

import (
	nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/modules"
)

func TemporarilyBanPlayer(client *nanmanager.NanCityManagerClient, playerID int32, reason string) error {
	durationSeconds := int64(3600)
	return client.Players.BanPlayer(playerID, modules.BanPlayerRequest{
		BanMode: modules.BanModeTemporary,
		DurationSeconds: &durationSeconds,
		Reason: reason,
	})
}
```

## 服务器详情

```go
package examples

import (
	nanmanager "github.com/NewNanCity/NewNanManager-SDK/clients/golang"
	"github.com/NewNanCity/NewNanManager-SDK/clients/golang/modules"
)

func RegisterAndInspectServer(client *nanmanager.NanCityManagerClient, name, address string) (*modules.ServerDetailData, error) {
	server, err := client.Servers.CreateServer(modules.CreateServerRequest{Name: name, Address: address})
	if err != nil {
		return nil, err
	}
	return client.Servers.GetServer(server.ID, true)
}
```

`detail.Status` 可能为空，读取前需要判断。列表接口使用当前模块的方法签名；安装来源需按实际工作区或已确认的包发布版本配置，本轮不证明公共仓库路径可直接获取。
