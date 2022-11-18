package cn.sunnysky.api;

public interface IServer {
    void onCommonCommand(String cmd);
    void onCalculate();
    void dropMenu();
    void shutdown();
}
