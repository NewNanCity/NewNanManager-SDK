"""IP-related data models."""

from typing import Optional

from pydantic import BaseModel, Field

from .common import PagedData
from .enums import ThreatLevel, QueryStatus


class IPInfo(BaseModel):
    """IP信息."""

    ip: str = Field(description="IP地址")
    ip_type: str = Field(description="IP类型：ipv4/ipv6")
    country: Optional[str] = Field(default=None, description="国家/地区")
    country_code: Optional[str] = Field(default=None, description="国家代码")
    region: Optional[str] = Field(default=None, description="省份/州")
    city: Optional[str] = Field(default=None, description="城市")
    latitude: Optional[float] = Field(default=None, description="纬度")
    longitude: Optional[float] = Field(default=None, description="经度")
    timezone: Optional[str] = Field(default=None, description="时区")
    isp: Optional[str] = Field(default=None, description="网络服务提供商")
    organization: Optional[str] = Field(default=None, description="组织名称")
    asn: Optional[str] = Field(default=None, description="ASN号码")
    is_bogon: bool = Field(description="是否为Bogon IP")
    is_mobile: bool = Field(description="是否为移动网络")
    is_satellite: bool = Field(description="是否为卫星网络")
    is_crawler: bool = Field(description="是否为爬虫")
    is_datacenter: bool = Field(description="是否为数据中心IP")
    is_tor: bool = Field(description="是否为Tor出口节点")
    is_proxy: bool = Field(description="是否为代理IP")
    is_vpn: bool = Field(description="是否为VPN")
    is_abuser: bool = Field(description="是否为滥用者")
    banned: bool = Field(description="是否被封禁")
    ban_reason: Optional[str] = Field(default=None, description="封禁原因")
    threat_level: ThreatLevel = Field(description="威胁等级")
    risk_score: int = Field(description="风险评分（0-100）")
    query_status: QueryStatus = Field(description="查询状态")
    last_query_at: Optional[str] = Field(
        default=None, description="最后查询时间(ISO8601格式)"
    )
    created_at: str = Field(description="创建时间(ISO8601格式)")
    updated_at: str = Field(description="更新时间(ISO8601格式)")
    # 风险信息字段
    risk_level: str = Field(description="风险等级")
    risk_description: str = Field(description="风险描述")


class IPStatistics(BaseModel):
    """IP统计信息."""

    total_ips: int = Field(description="总IP数")
    completed_ips: int = Field(description="已完成查询的IP数")
    pending_ips: int = Field(description="待查询IP数")
    failed_ips: int = Field(description="查询失败IP数")
    banned_ips: int = Field(description="被封禁IP数")
    proxy_ips: int = Field(description="代理IP数")
    vpn_ips: int = Field(description="VPN IP数")
    tor_ips: int = Field(description="Tor IP数")
    datacenter_ips: int = Field(description="数据中心IP数")
    high_risk_ips: int = Field(description="高风险IP数")


class IPsListData(PagedData[IPInfo]):
    """IP列表数据."""

    ips: list[IPInfo] = Field(default_factory=list, description="IP列表")

    @property
    def items(self) -> list[IPInfo]:
        """获取数据项列表."""
        return self.ips

    @items.setter
    def items(self, value: list[IPInfo]) -> None:
        """设置数据项列表."""
        self.ips = value
