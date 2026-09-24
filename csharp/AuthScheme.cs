namespace NewNanManager.Client;

/// <summary>
/// Credential header selected by the client.
/// </summary>
public enum AuthScheme
{
    /// <summary>Authorization: Bearer &lt;token&gt;.</summary>
    Bearer,

    /// <summary>X-API-Token: &lt;token&gt;.</summary>
    ApiToken,
}
