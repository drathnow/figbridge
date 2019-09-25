package zedi.pacbridge.stp.fad;

interface ResendMessageStrategy {
    public boolean canResendMessage(InTransitMessage inTransitMessage);
}