using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// 城镇实体
/// </summary>
public class Town
{
    /// <summary>
    /// 城镇ID
    /// </summary>
    [JsonPropertyName("id")]
    public int Id { get; set; }

    /// <summary>
    /// 城镇名称
    /// </summary>
    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// 城镇等级
    /// </summary>
    [JsonPropertyName("level")]
    public int Level { get; set; }

    /// <summary>
    /// 城主ID
    /// </summary>
    [JsonPropertyName("leader_id")]
    public int? LeaderId { get; set; }

    /// <summary>
    /// QQ群号
    /// </summary>
    [JsonPropertyName("qq_group")]
    public string? QQGroup { get; set; }

    /// <summary>
    /// 城镇描述
    /// </summary>
    [JsonPropertyName("description")]
    public string? Description { get; set; }

    /// <summary>
    /// 创建时间
    /// </summary>
    [JsonPropertyName("created_at")]
    public DateTime CreatedAt { get; set; }

    /// <summary>
    /// 更新时间
    /// </summary>
    [JsonPropertyName("updated_at")]
    public DateTime UpdatedAt { get; set; }
}

/// <summary>
/// 城镇列表数据
/// </summary>
public class TownsListData : PagedData<Town>
{
    /// <summary>
    /// 城镇列表
    /// </summary>
    [JsonPropertyName("towns")]
    public List<Town> Towns
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 城镇成员数据
/// </summary>
public class TownMembersData : PagedData<Player>
{
    /// <summary>
    /// 成员列表
    /// </summary>
    [JsonPropertyName("members")]
    public List<Player> Members
    {
        get => Items;
        set => Items = value;
    }
}

/// <summary>
/// 城镇详细信息响应
/// </summary>
public class TownDetailResponse
{
    /// <summary>
    /// 城镇基本信息
    /// </summary>
    [JsonPropertyName("town")]
    public Town Town { get; set; } = new();

    /// <summary>
    /// 镇长ID
    /// </summary>
    [JsonPropertyName("leader")]
    public int? Leader { get; set; } // 符合IDL中的 optional i32 leader 定义

    /// <summary>
    /// 成员列表（包括镇长）
    /// </summary>
    [JsonPropertyName("members")]
    public List<Player> Members { get; set; } = new();
}
