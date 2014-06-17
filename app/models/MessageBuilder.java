package models;

public class MessageBuilder {

    public void MessageBuilder(){

    }

    public void build(long from, long to, int height, String attachment_bytes){

        String message = NxtParser.convertHexToString(attachment_bytes);

        if (from == to) { // skip messages sent to self
            return;
        }

        if (NxtParser.isBinaryMessage(message)) {
            return;
        }

        if (message.trim().isEmpty()) {
            return;
        }

        //_textMessageCount++;

        if (NxtParser.useReedSolomonAddresses) {
            String s = "NXT-" + crypto.ReedSolomon.encode(from);
            String r = "NXT-" + crypto.ReedSolomon.encode(to);

        } else {
            //AddMessage(Long.toString(sender), Long.toString(recipient), message, height);
        }
    }

}
