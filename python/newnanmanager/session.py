"""Server session fencing primitives."""

from pydantic import BaseModel, ConfigDict, Field, field_validator


class SessionContext(BaseModel):
    """Server-instance session tuple issued by NewNanManager."""

    model_config = ConfigDict(frozen=True)

    id: str = Field(
        min_length=32,
        max_length=64,
        description="服务端签发的服务器实例会话标识",
    )
    epoch: int = Field(gt=0, description="单调递增会话代次")

    @field_validator("id")
    @classmethod
    def validate_id(cls, value: str) -> str:
        if not value.strip():
            raise ValueError("session id must not be blank")
        return value

    def headers(self) -> dict[str, str]:
        """Return the fencing headers for one request."""

        return {
            "X-NNM-Session-ID": self.id,
            "X-NNM-Session-Epoch": str(self.epoch),
        }
