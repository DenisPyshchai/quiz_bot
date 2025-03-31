package com.web.quiz_bot.ssh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class SftpServer {

    private final Log log = LogFactory.getLog(SftpServer.class);

    @PostConstruct
    public void startServer() throws IOException {
        start();
    }

    private void start() throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2222);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("/ssh/id_rsa")));
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setPasswordAuthenticator((username, password, session) -> username.equals("admin") && password.equals("password"));
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(Paths.get("/ssh/id_rsa.pub")));
        sshd.start();
        log.info("SFTP server started");
    }
}
