# 开线程跑任务参考如下
````

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Component
@EnableScheduling
@RequiredArgsConstructor
public class MsgExpireMap implements CommandLineRunner {

    public static volatile Map<Long, Date> msgMap = new ConcurrentHashMap<>();
    private static volatile ReentrantLock myMsgLock = new ReentrantLock();
    public static final String w = "w";
    private static volatile int expireTime = 15;
    private static int m = 1000 * 60;
    private static int readers = 0;
    private static int writers = 0;
    private static int index = 0;

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    @Override
    public void run(String... args) {
        new MsgThread().start();
    }

    private class MsgThread extends Thread {

        @Override
        public void run() {
            while (true) synchronized (w) {
                try {
                    Iterator<Map.Entry<Long, Date>> iterator = msgMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        lockRead();
                        Map.Entry<Long, Date> next = iterator.next();
                        Long baseId = next.getKey();
                        try {
                            Date date = new Date();
                            Date sendTime = next.getValue();
                            if (null == sendTime) {
                                iterator.remove();
                            } else {
                                long timeDiff = (date.getTime() - sendTime.getTime()) / m;
//                                log.info("\n——CHECKTIMEDIFF baseId:{} now:{} sendTime:{} timeDiff:{}", baseId, timeFormat.format(date), timeFormat.format(sendTime), timeDiff);
                                if (timeDiff >= expireTime) {
                                    send(baseId);
//                                    log.info("\n等待内部调用结果 baseId:{} now:{} sendTime:{} timeDiff:{}", baseId, timeFormat.format(date), timeFormat.format(sendTime), timeDiff);
                                    iterator.remove();
                                }
                            }
                        } catch (Exception e) {
                            log.error("messageMap.entrySet().iterator() 异常  baseId:{}", baseId, e);
                        }
                        unlockRead();
                    }
                    if (msgMap.size() == 0) {
                        w.wait();
                    } else {
                        w.wait(1000 * 10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SneakyThrows
    public void put(Long baseId, Date sendTime) {
        lockWrite();
        msgMap.put(baseId, sendTime);
        unlockWrite();
    }

    @SneakyThrows
    public void finish(Long baseId) {
        lockWrite();
        msgMap.put(baseId, null);
        unlockWrite();
    }

    private void lockRead() throws InterruptedException {
        while (writers > 0 || readers > 0) synchronized (w) {
            w.wait();
        }
        readers++;
        myMsgLock.lock();
    }

    private void unlockRead() {
        synchronized (w) {
            readers--;
            myMsgLock.unlock();
            w.notifyAll();
        }
    }

    private void lockWrite() throws InterruptedException {
        while (readers > 0 || writers > 0) synchronized (w) {
            w.wait();
        }
        writers++;
        myMsgLock.lock();
    }

    private void unlockWrite() throws InterruptedException {
        synchronized (w) {
            writers--;
            myMsgLock.unlock();
            w.notifyAll();
        }
    }

    private void send(Long baseId) {

    }

}
````