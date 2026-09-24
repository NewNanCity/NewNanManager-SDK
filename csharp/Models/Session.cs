using System.Text.Json.Serialization;

namespace NewNanManager.Client.Models;

/// <summary>
/// Server-instance session tuple issued by NewNanManager.
/// </summary>
public sealed class SessionContext
{
    public SessionContext(string id, long epoch)
    {
        if (string.IsNullOrWhiteSpace(id))
            throw new ArgumentException("Session id cannot be blank.", nameof(id));
        if (id.Length is < 32 or > 64)
            throw new ArgumentException("Session id length must be between 32 and 64 characters.", nameof(id));
        if (epoch <= 0)
            throw new ArgumentOutOfRangeException(nameof(epoch), "Session epoch must be greater than zero.");

        Id = id;
        Epoch = epoch;
    }

    /// <summary>Server-issued session identifier.</summary>
    public string Id { get; }

    /// <summary>Server-issued monotonic session epoch.</summary>
    public long Epoch { get; }

    internal IReadOnlyDictionary<string, string> Headers => new Dictionary<string, string>
    {
        ["X-NNM-Session-ID"] = Id,
        ["X-NNM-Session-Epoch"] = Epoch.ToString(System.Globalization.CultureInfo.InvariantCulture),
    };
}

/// <summary>
/// Wire response returned when a server session is created.
/// </summary>
public sealed class ServerSessionResponse
{
    [JsonPropertyName("session_id")]
    public string SessionId { get; set; } = string.Empty;

    [JsonPropertyName("session_epoch")]
    public long SessionEpoch { get; set; }

    internal SessionContext ToContext() => new(SessionId, SessionEpoch);
}
