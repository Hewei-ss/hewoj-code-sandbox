package com.hew.hewojcodesandbox;

import cn.hutool.core.util.StrUtil;
import com.hew.hewojcodesandbox.model.ExecuteCodeRequest;
import com.hew.hewojcodesandbox.model.ExecuteCodeResponse;
import com.hew.hewojcodesandbox.model.ExecuteMessage;
import com.hew.hewojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hew.hewojcodesandbox.constant.SandBoxConstants;

import static com.hew.hewojcodesandbox.constant.SandBoxConstants.TIME_OUT;

/**
 * Java 原生代码沙箱实现（直接复用模板方法）
 */
@Component
@Slf4j
public class JavaNativeACMCodesandbox extends JavaCodeSandboxTemplate {

    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) throws IOException {

        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeResults = new ArrayList<>();
        for (String input : inputList) {
            //Linux下的命令
            // String runCmd = String.format("/software/jdk1.8.0_361/bin/java -Xmx256m -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            //Windows下的命令
            //    String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            String dir=userCodeFile.getAbsolutePath();
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath);
         //   String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, input);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Process runProcess = Runtime.getRuntime().exec(runCmd);
            // 超时控制
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TIME_OUT);
                    //超时了
                    runProcess.destroy();
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                }
            });
            thread.start();

            ExecuteMessage executeMessage = null;
            try {
                executeMessage = ProcessUtils.getAcmProcessMessage(runProcess, input);
            } catch (IOException e){
                log.error("执行出错: {}", e.toString());
            }
            stopWatch.stop();
            if(!thread.isAlive()){
                executeMessage = new ExecuteMessage();
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessage.setErrorMessage("超出时间限制");
            }
            executeResults.add(executeMessage);

            //已经有用例失败了
            if(StrUtil.isNotBlank(executeMessage.getErrorMessage())){
                break;
            }
        }
        return executeResults;
    }
}
