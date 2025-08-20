/**
 * Jest测试环境设置
 */

// 设置测试超时时间
jest.setTimeout(10000);

// 模拟console方法以避免测试输出过多日志
const originalConsole = console;

beforeAll(() => {
  // 在测试期间静默console输出
  global.console = {
    ...originalConsole,
    log: jest.fn(),
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn()
  } as any;
});

afterAll(() => {
  // 恢复原始console
  global.console = originalConsole;
});
