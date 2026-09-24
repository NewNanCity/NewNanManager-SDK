namespace NewNanManager.Client.Exceptions;

/// <summary>
/// NewNanManager API异常基类
/// </summary>
public class NewNanManagerException : Exception
{
    /// <summary>
    /// 错误代码
    /// </summary>
    public int ErrorCode { get; }

    /// <summary>
    /// 请求ID
    /// </summary>
    public string? RequestId { get; }

    /// <summary>
    /// 错误详情
    /// </summary>
    public string? Details { get; }

    /// <summary>Stable numeric code from the API error registry.</summary>
    public int? ApiCode { get; }

    /// <summary>Stable error category from the API error registry.</summary>
    public string? ErrorCategory { get; }

    /// <summary>Stable machine-readable error code.</summary>
    public string? MachineCode { get; }

    /// <summary>Trace identifier returned by the server.</summary>
    public string? TraceId { get; }

    /// <summary>Retry-After response header, preserved without guessing its format.</summary>
    public string? RetryAfter { get; init; }

    public NewNanManagerException(string message)
        : base(message)
    {
        ErrorCode = -1;
    }

    public NewNanManagerException(string message, Exception innerException)
        : base(message, innerException)
    {
        ErrorCode = -1;
    }

    public NewNanManagerException(
        int errorCode,
        string message,
        string? requestId = null,
        string? details = null,
        int? apiCode = null,
        string? errorCategory = null,
        string? machineCode = null,
        string? traceId = null
    )
        : base(message)
    {
        ErrorCode = errorCode;
        RequestId = requestId;
        Details = details;
        ApiCode = apiCode;
        ErrorCategory = errorCategory;
        MachineCode = machineCode;
        TraceId = traceId;
    }

    public NewNanManagerException(
        int errorCode,
        string message,
        Exception innerException,
        string? requestId = null,
        string? details = null
    )
        : base(message, innerException)
    {
        ErrorCode = errorCode;
        RequestId = requestId;
        Details = details;
    }

    public override string ToString()
    {
        var result = base.ToString();

        if (!string.IsNullOrEmpty(RequestId))
        {
            result += $"\nRequest ID: {RequestId}";
        }

        if (!string.IsNullOrEmpty(Details))
        {
            result += $"\nDetails: {Details}";
        }

        return result;
    }
}

/// <summary>
/// HTTP请求异常
/// </summary>
public class NewNanManagerHttpException : NewNanManagerException
{
    /// <summary>
    /// HTTP状态码
    /// </summary>
    public int StatusCode { get; }

    public NewNanManagerHttpException(int statusCode, string message)
        : base(message)
    {
        StatusCode = statusCode;
    }

    public NewNanManagerHttpException(int statusCode, string message, Exception innerException)
        : base(message, innerException)
    {
        StatusCode = statusCode;
    }

    public NewNanManagerHttpException(int statusCode, string message, string? requestId, string? retryAfter)
        : base(statusCode, message, requestId)
    {
        StatusCode = statusCode;
        RetryAfter = retryAfter;
    }
}

/// <summary>
/// API错误异常
/// </summary>
public class ApiErrorException : NewNanManagerException
{
    public int StatusCode => ErrorCode;

    public ApiErrorException(
        int errorCode,
        string message,
        string? requestId = null,
        string? details = null,
        int? apiCode = null,
        string? errorCategory = null,
        string? machineCode = null,
        string? traceId = null
    )
        : base(errorCode, message, requestId, details, apiCode, errorCategory, machineCode, traceId) { }
}
