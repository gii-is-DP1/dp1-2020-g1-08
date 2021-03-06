import ApiUtils from "./ApiUtils";
const MESSAGE_URL = "/message";

const MessageApiUtils = {
  /*Inboxes*/
  getMyInboxMessages: () => ApiUtils.get(MESSAGE_URL + "/inbox"),
  getMySentMessages: () => ApiUtils.get(MESSAGE_URL + "/sent"),
  getMyMessagesByTag: (tagId) =>
    ApiUtils.get(MESSAGE_URL + "/byTag?tagId=" + tagId),
  getMyMessagesBySearch: (search) =>
    ApiUtils.get(MESSAGE_URL + "/bySearch?search=" + search),
  getNumberOfNotReadMessages: () => ApiUtils.get(MESSAGE_URL + "/noRead"),
  getNumberOfNoReadMessagesByTag: (tagId) =>
    ApiUtils.get(MESSAGE_URL + "/noReadByTag?tagId=" + tagId),

  /*Messages*/
  newMessage: (message) => ApiUtils.post(MESSAGE_URL, message),
  replyMessage: (message, repliedMessageId) =>
    ApiUtils.post(
      MESSAGE_URL + "/reply?repliedMessageId=" + repliedMessageId,
      message
    ),
  forwardMessage: (forwardList, forwardedMessageId) =>
    ApiUtils.post(
      MESSAGE_URL + "/forward?forwardedMessageId=" + forwardedMessageId,
      forwardList
    ),
  markMessageAsRead: (messageId) =>
    ApiUtils.post(MESSAGE_URL + "/markAsRead?messageId=" + messageId),
};

export default MessageApiUtils;
