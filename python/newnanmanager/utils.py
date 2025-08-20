"""Utility functions for NewNanManager SDK."""

import asyncio
import logging
from typing import Callable, TypeVar

logger = logging.getLogger(__name__)

T = TypeVar("T")


async def retry_with_backoff(
    func: Callable[..., T],
    max_retries: int = 3,
    base_delay: float = 1.0,
    max_delay: float = 60.0,
    backoff_factor: float = 2.0,
    exceptions: tuple[type[Exception], ...] = (Exception,),
) -> T:
    """使用指数退避重试函数.

    Args:
        func: 要重试的函数
        max_retries: 最大重试次数
        base_delay: 基础延迟时间（秒）
        max_delay: 最大延迟时间（秒）
        backoff_factor: 退避因子
        exceptions: 需要重试的异常类型

    Returns:
        函数执行结果

    Raises:
        最后一次执行的异常
    """
    last_exception = None

    for attempt in range(max_retries + 1):
        try:
            if asyncio.iscoroutinefunction(func):
                return await func()  # type: ignore
            else:
                return func()  # type: ignore
        except exceptions as e:
            last_exception = e

            if attempt < max_retries:
                delay = min(base_delay * (backoff_factor**attempt), max_delay)
                logger.warning(
                    f"Attempt {attempt + 1} failed, retrying in {delay:.2f}s: {e}"
                )
                await asyncio.sleep(delay)
            else:
                logger.error(f"All {max_retries + 1} attempts failed")
                break

    if last_exception:
        raise last_exception
    else:
        raise RuntimeError("Unexpected error in retry logic")


def sanitize_url(url: str) -> str:
    """清理URL，移除敏感信息用于日志记录.

    Args:
        url: 原始URL

    Returns:
        清理后的URL
    """
    # 简单的URL清理，移除查询参数中的敏感信息
    if "?" in url:
        base_url, query = url.split("?", 1)
        # 这里可以添加更复杂的查询参数过滤逻辑
        return f"{base_url}?[QUERY_PARAMS]"
    return url


def format_duration(seconds: float) -> str:
    """格式化持续时间.

    Args:
        seconds: 秒数

    Returns:
        格式化的时间字符串
    """
    if seconds < 1:
        return f"{seconds * 1000:.0f}ms"
    elif seconds < 60:
        return f"{seconds:.2f}s"
    else:
        minutes = int(seconds // 60)
        remaining_seconds = seconds % 60
        return f"{minutes}m{remaining_seconds:.1f}s"
