/**
 * IP管理服务模块
 * 基于 @sttot/axios-api 的标准化API定义
 */

import { apiBase } from '@sttot/axios-api';
import {
  IPInfo,
  BanIPRequest,
  UnbanIPRequest,
  ListIPsRequest,
  ListIPsResponse,
  ListBannedIPsRequest,
  ListBannedIPsResponse,
  IPStatistics,
  EmptyResponse
} from '../types';
import { commonErrorHandler } from '../utils/errorHandler';

export const initIPService = (apiFactory: ReturnType<typeof apiBase>) => {
  class IPService {
    // 获取IP信息
    public getIPInfo = apiFactory<{ ip: string }, IPInfo>(
      (request) => ({
        method: 'GET',
        url: `/api/v1/ips/${request.ip}`
      }),
      ({ data }) => ({
        ip: data.ip,
        ipType: data.ip_type,
        country: data.country,
        countryCode: data.country_code,
        region: data.region,
        city: data.city,
        latitude: data.latitude,
        longitude: data.longitude,
        timezone: data.timezone,
        isp: data.isp,
        organization: data.organization,
        asn: data.asn,
        isBogon: data.is_bogon,
        isMobile: data.is_mobile,
        isSatellite: data.is_satellite,
        isCrawler: data.is_crawler,
        isDatacenter: data.is_datacenter,
        isTor: data.is_tor,
        isProxy: data.is_proxy,
        isVpn: data.is_vpn,
        isAbuser: data.is_abuser,
        banned: data.banned,
        banReason: data.ban_reason,
        threatLevel: data.threat_level,
        riskScore: data.risk_score,
        queryStatus: data.query_status,
        lastQueryAt: data.last_query_at,
        createdAt: data.created_at,
        updatedAt: data.updated_at,
        riskLevel: data.risk_level,
        riskDescription: data.risk_description
      }),
      commonErrorHandler
    );

    // 封禁IP（支持批量）
    public banIP = apiFactory<BanIPRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/ips/ban',
        data: {
          ips: request.ips,
          reason: request.reason
        }
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 解封IP（支持批量）
    public unbanIP = apiFactory<UnbanIPRequest, EmptyResponse>(
      (request) => ({
        method: 'POST',
        url: '/api/v1/ips/unban',
        data: {
          ips: request.ips
        }
      }),
      ({ data }) => data as EmptyResponse,
      commonErrorHandler
    );

    // 获取被封禁的IP列表
    public listBannedIPs = apiFactory<ListBannedIPsRequest, ListBannedIPsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/ips/banned',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          active_only: request.activeOnly
        })
      }),
      ({ data }) => ({
        bans: data.ips.map((ip: any) => ({
          ip: ip.ip,
          reason: ip.ban_reason || '',
          bannedAt: ip.created_at,
          unbannedAt: undefined,
          unbanReason: undefined,
          active: ip.banned
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取IP列表
    public listIPs = apiFactory<ListIPsRequest, ListIPsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/ips',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          banned_only: request.bannedOnly,
          min_threat_level: request.minThreatLevel,
          min_risk_score: request.minRiskScore
        })
      }),
      ({ data }) => ({
        ips: data.ips.map((ip: any) => ({
          ip: ip.ip,
          ipType: ip.ip_type,
          country: ip.country,
          countryCode: ip.country_code,
          region: ip.region,
          city: ip.city,
          latitude: ip.latitude,
          longitude: ip.longitude,
          timezone: ip.timezone,
          isp: ip.isp,
          organization: ip.organization,
          asn: ip.asn,
          isBogon: ip.is_bogon,
          isMobile: ip.is_mobile,
          isSatellite: ip.is_satellite,
          isCrawler: ip.is_crawler,
          isDatacenter: ip.is_datacenter,
          isTor: ip.is_tor,
          isProxy: ip.is_proxy,
          isVpn: ip.is_vpn,
          isAbuser: ip.is_abuser,
          banned: ip.banned,
          banReason: ip.ban_reason,
          threatLevel: ip.threat_level,
          riskScore: ip.risk_score,
          queryStatus: ip.query_status,
          lastQueryAt: ip.last_query_at,
          createdAt: ip.created_at,
          updatedAt: ip.updated_at,
          riskLevel: ip.risk_level,
          riskDescription: ip.risk_description
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取可疑IP列表
    public getSuspiciousIPs = apiFactory<ListIPsRequest, ListIPsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/ips/suspicious',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          banned_only: request.bannedOnly,
          min_threat_level: request.minThreatLevel,
          min_risk_score: request.minRiskScore
        })
      }),
      ({ data }) => ({
        ips: data.ips.map((ip: any) => ({
          ip: ip.ip,
          ipType: ip.ip_type,
          country: ip.country,
          countryCode: ip.country_code,
          region: ip.region,
          city: ip.city,
          latitude: ip.latitude,
          longitude: ip.longitude,
          timezone: ip.timezone,
          isp: ip.isp,
          organization: ip.organization,
          asn: ip.asn,
          isBogon: ip.is_bogon,
          isMobile: ip.is_mobile,
          isSatellite: ip.is_satellite,
          isCrawler: ip.is_crawler,
          isDatacenter: ip.is_datacenter,
          isTor: ip.is_tor,
          isProxy: ip.is_proxy,
          isVpn: ip.is_vpn,
          isAbuser: ip.is_abuser,
          banned: ip.banned,
          banReason: ip.ban_reason,
          threatLevel: ip.threat_level,
          riskScore: ip.risk_score,
          queryStatus: ip.query_status,
          lastQueryAt: ip.last_query_at,
          createdAt: ip.created_at,
          updatedAt: ip.updated_at,
          riskLevel: ip.risk_level,
          riskDescription: ip.risk_description
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取高风险IP列表
    public getHighRiskIPs = apiFactory<ListIPsRequest, ListIPsResponse>(
      (request = {}) => ({
        method: 'GET',
        url: '/api/v1/ips/high-risk',
        params: this.buildParams({
          page: request.page,
          page_size: request.pageSize,
          banned_only: request.bannedOnly,
          min_threat_level: request.minThreatLevel,
          min_risk_score: request.minRiskScore
        })
      }),
      ({ data }) => ({
        ips: data.ips.map((ip: any) => ({
          ip: ip.ip,
          ipType: ip.ip_type,
          country: ip.country,
          countryCode: ip.country_code,
          region: ip.region,
          city: ip.city,
          latitude: ip.latitude,
          longitude: ip.longitude,
          timezone: ip.timezone,
          isp: ip.isp,
          organization: ip.organization,
          asn: ip.asn,
          isBogon: ip.is_bogon,
          isMobile: ip.is_mobile,
          isSatellite: ip.is_satellite,
          isCrawler: ip.is_crawler,
          isDatacenter: ip.is_datacenter,
          isTor: ip.is_tor,
          isProxy: ip.is_proxy,
          isVpn: ip.is_vpn,
          isAbuser: ip.is_abuser,
          banned: ip.banned,
          banReason: ip.ban_reason,
          threatLevel: ip.threat_level,
          riskScore: ip.risk_score,
          queryStatus: ip.query_status,
          lastQueryAt: ip.last_query_at,
          createdAt: ip.created_at,
          updatedAt: ip.updated_at,
          riskLevel: ip.risk_level,
          riskDescription: ip.risk_description
        })),
        total: data.total,
        page: data.page,
        pageSize: data.page_size
      }),
      commonErrorHandler
    );

    // 获取IP统计信息
    public getIPStatistics = apiFactory<{}, IPStatistics>(
      () => ({
        method: 'GET',
        url: '/api/v1/ips/statistics'
      }),
      ({ data }) => ({
        totalIps: data.total_ips,
        completedIps: data.completed_ips,
        pendingIps: data.pending_ips,
        failedIps: data.failed_ips,
        bannedIps: data.banned_ips,
        proxyIps: data.proxy_ips,
        vpnIps: data.vpn_ips,
        torIps: data.tor_ips,
        datacenterIps: data.datacenter_ips,
        highRiskIps: data.high_risk_ips
      }),
      commonErrorHandler
    );

    // 辅助方法：构建查询参数，过滤掉undefined值
    public buildParams(params: Record<string, any>): Record<string, any> {
      const result: Record<string, any> = {};
      for (const [key, value] of Object.entries(params)) {
        if (value !== undefined && value !== null) {
          result[key] = value;
        }
      }
      return result;
    }
  }

  return new IPService();
};
