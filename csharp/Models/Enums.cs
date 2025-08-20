using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 封禁模式枚举
/// </summary>
public enum BanMode
{
    /// <summary>
    /// 正常状态（未封禁）
    /// </summary>
    Normal = 0,

    /// <summary>
    /// 临时封禁
    /// </summary>
    Temporary = 1,

    /// <summary>
    /// 永久封禁
    /// </summary>
    Permanent = 2,
}

/// <summary>
/// 登录动作枚举
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum LoginAction
{
    /// <summary>
    /// 登录
    /// </summary>
    Login,

    /// <summary>
    /// 登出
    /// </summary>
    Logout,
}

/// <summary>
/// 服务器类型枚举
/// </summary>
[JsonConverter(typeof(JsonStringEnumConverter))]
public enum ServerType
{
    /// <summary>
    /// Minecraft游戏服务器
    /// </summary>
    Minecraft,

    /// <summary>
    /// 代理服务器
    /// </summary>
    Proxy,

    /// <summary>
    /// 大厅服务器
    /// </summary>
    Lobby,
}
