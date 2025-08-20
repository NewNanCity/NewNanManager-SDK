/**
 * NewNanManager API 枚举类型定义
 */

/**
 * 封禁模式枚举
 */
export enum BanMode {
  /** 正常 */
  NORMAL = 0,
  /** 临时封禁 */
  TEMPORARY = 1,
  /** 永久封禁 */
  PERMANENT = 2
}

/**
 * 登录动作枚举
 */
export enum LoginAction {
  LOGIN = 1,
  LOGOUT = 2
}



/**
 * 枚举值转换工具函数
 */
export const EnumUtils = {
  /**
   * 从数值获取BanMode枚举
   */
  getBanMode(value: number): BanMode {
    if (Object.values(BanMode).includes(value as BanMode)) {
      return value as BanMode;
    }
    throw new Error(`Invalid BanMode value: ${value}`);
  },

  /**
   * 从数值获取LoginAction枚举
   */
  getLoginAction(value: number): LoginAction {
    if (Object.values(LoginAction).includes(value as LoginAction)) {
      return value as LoginAction;
    }
    throw new Error(`Invalid LoginAction value: ${value}`);
  },


};
