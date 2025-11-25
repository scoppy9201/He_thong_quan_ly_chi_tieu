/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MILLIS = TimeUnit.MINUTES.toMillis(3); // 3 phút

    private static class AttemptInfo {
        int attempts;
        long lockUntil; // epoch millis, 0 nếu không khóa
    }

    private final ConcurrentHashMap<String, AttemptInfo> attemptsMap = new ConcurrentHashMap<>();

    public void recordFailedAttempt(String email) {
        AttemptInfo info = attemptsMap.computeIfAbsent(email.toLowerCase(), k -> new AttemptInfo());
        synchronized (info) {
            if (isLocked(email)) return; // đang khóa thì không tăng nữa
            info.attempts++;
            if (info.attempts >= MAX_ATTEMPTS) {
                info.lockUntil = System.currentTimeMillis() + LOCK_TIME_MILLIS;
            }
        }
    }

    public void resetAttempts(String email) {
        attemptsMap.remove(email.toLowerCase());
    }

    public boolean isLocked(String email) {
        AttemptInfo info = attemptsMap.get(email.toLowerCase());
        if (info == null) return false;
        synchronized (info) {
            if (info.lockUntil == 0) return false;
            if (System.currentTimeMillis() > info.lockUntil) {
                // hết thời gian khóa -> reset
                attemptsMap.remove(email.toLowerCase());
                return false;
            }
            return true;
        }
    }

    public long getRemainingLockMillis(String email) {
        AttemptInfo info = attemptsMap.get(email.toLowerCase());
        if (info == null) return 0;
        synchronized (info) {
            if (info.lockUntil == 0) return 0;
            long remain = info.lockUntil - System.currentTimeMillis();
            return Math.max(remain, 0);
        }
    }

    public int getAttempts(String email) {
        AttemptInfo info = attemptsMap.get(email.toLowerCase());
        return info == null ? 0 : info.attempts;
    }
}

