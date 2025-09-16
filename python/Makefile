.PHONY: help install install-dev test test-cov lint format type-check clean build upload docs

help:  ## 显示帮助信息
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

install:  ## 安装包
	pip install -e .

install-dev:  ## 安装开发依赖
	pip install -e ".[dev]"

test:  ## 运行测试
	pytest

test-cov:  ## 运行测试并生成覆盖率报告
	pytest --cov=newnanmanager --cov-report=html --cov-report=term

test-integration:  ## 运行集成测试（需要设置环境变量）
	pytest tests/test_integration.py -v

lint:  ## 运行代码检查
	ruff check newnanmanager tests examples
	black --check newnanmanager tests examples
	isort --check-only newnanmanager tests examples

format:  ## 格式化代码
	black newnanmanager tests examples
	isort newnanmanager tests examples
	ruff check --fix newnanmanager tests examples

type-check:  ## 运行类型检查
	mypy newnanmanager

clean:  ## 清理构建文件
	rm -rf build/
	rm -rf dist/
	rm -rf *.egg-info/
	rm -rf .pytest_cache/
	rm -rf .coverage
	rm -rf htmlcov/
	find . -type d -name __pycache__ -exec rm -rf {} +
	find . -type f -name "*.pyc" -delete

build:  ## 构建包
	python -m build

upload:  ## 上传到PyPI
	python -m twine upload dist/*

upload-test:  ## 上传到测试PyPI
	python -m twine upload --repository testpypi dist/*

docs:  ## 生成文档
	mkdocs build

docs-serve:  ## 启动文档服务器
	mkdocs serve

all: format lint type-check test  ## 运行所有检查
