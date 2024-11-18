package com.bio.drqi.manage.configuration;

import cn.hutool.extra.mail.MailAccount;
import com.bio.common.core.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CerConfiguration {


    @Value("${cer.properties.email.emailAccount}")
    private String emailAccount;

    @Value("${cer.properties.email.emailPassword}")
    private String emailPassword;

    @Bean
    public MailAccount initMailAccount() {
        MailAccount account = new MailAccount();
        account.setAuth(true);
        account.setFrom(emailAccount);
        account.setPass(EncryptUtil.decryptSm4(emailPassword));
        return account;
    }
}
