package cn.chnsys.netty.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public boolean isSuccess() {
        return super.isSuccess();
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
