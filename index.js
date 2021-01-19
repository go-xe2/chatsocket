
import { NativeModules } from 'react-native';
const { RNChatSocket } = NativeModules;


/**
 * 获取chatsocket版本
 * @returns {*}
 */
const getVersion = () => {
    return RNChatSocket.getVersion();
};

/**
 * 获取IM服务配置地址
 * @returns {*}
 */
const getIMHost =  () => {
    return RNChatSocket.getIMHost();
};

/**
 * 获取设备id
 * @returns {*}
 */
const  getDeviceID = () => {
    return RNChatSocket.getDeviceID();
};

const getDeviceInfo = async () => {
    return await RNChatSocket.getDeviceInfo();
};

/**
 * 保存设备信息
 * @param uuid
 * @param brand
 * @param mode
 * @param sysVersion
 * @param sdkVersion
 * @returns {Promise<*>}
 */
const saveDeviceInfo = async ({
    uuid = '',
    brand = '',
    mode = '',
    sysVersion = '',
    sdkVersion = '',
}) => {
    return await RNChatSocket.saveDeviceInfo({ uuid, brand, mode, sysVersion, sdkVersion });
};

/**
 * 获取IM的appKey配置
 * @returns {*}
 */
const getAppKey = () => {
    return RNChatSocket.getAppKey();
};

/**
 * 获取IM的appSecret配置
 * @returns {*}
 */
const getAppSecret = () => {
    return RNChatSocket.getAppSecret();
};
/**
 * 获取用户令牌
 * @returns {*}
 */
const  getUserToken = () => {
    return RNChatSocket.getUserToken();
};

/**
 * 获取用户id
 * @returns {*}
 */
const getUserID = () => {
    return RNChatSocket.getUserID();
};

/**
 * 获取用户昵称
 * @returns {*}
 */
const getUserNickName = () => {
    return RNChatSocket.getUserNickName();
};

/**
 * 获取用户头像
 * @returns {*}
 */
const getUserAvatarUrl = () => {
    return RNChatSocket.getUserAvatarUrl();
};

/**
 * 保存用户令牌
 * @param token
 * @returns {Promise<*>}
 */
const saveUserToken = async (token) => {
    return await RNChatSocket.saveUserToken(token);
};

/**
 * 保存用户id
 * @param userID
 * @returns {Promise<*>}
 */
const saveUserID = async (userID) => {
    return await RNChatSocket.saveUserID(userID);
};

/**
 * 保存用户昵称
 * @param nickName
 * @returns {Promise<*>}
 */
 const  saveUserNickName = async (nickName) => {
    return await RNChatSocket.saveUserNickName(nickName);
};

/**
 * 保存用户头像
 * @param avatarUrl
 * @returns {Promise<*>}
 */
const saveUserAvatarUrl = async (avatarUrl) => {
    return await RNChatSocket.saveUserAvatarUrl(avatarUrl);
}

const sendMessage = async ({
    token = '',
    receiverType = 1,
    receiverId = '',
    toUserIds = '',
    messageType = 1,
    content = '',
}) => {
    return await RNChatSocket.sendMessage({ token, receiverType, receiverId, toUserIds, messageType, content });
}

const sendChatEvent = async ({
    token = '',
    receiverId = '',
    event = 0,
}) => {
   return await RNChatSocket.sendChatEvent({ token, receiverId, event });
};

const setMessageRead = async ({
    token = '',
    msgId = 0,
}) => {
    return await RNChatSocket.setMessageRead({ token, msgId });
};

const getMessage = async ({
    token = '',
    msgId = 0,
}) => {
    return await RNChatSocket.getMessage({ token, msgId });
};

const getContactList = async ({
    token = '',
    search = '',
    pi = 1,
    ps = 50,
}) => {
    return await RNChatSocket.getContactList({ token, search, pi, ps });
};

const syncMessageList = async ({
    token = '',
    seq = 0,
    count = 100,
}) => {
    return await RNChatSocket.syncMessageList({ token, seq, count });
};

const delMessage = async ({
    token = '',
    msgId = 0,
}) => {
    return await RNChatSocket.delMessage({ token, msgId });
};

const registerDevice = async ({
    uuid = '',
    brand = '',
    mode = '',
    sysVersion = '',
    sdkVersion = '',
}) => {
    return await RNChatSocket.registerDevice({ uuid, brand, mode, sysVersion, sdkVersion });
};

const registerUser = async ({
    token = '',
    nickName = '',
    avatarUrl = '',
    sex = 0,
    extra = '',
    mobile = '',
}) => {
    return await RNChatSocket.registerUser({ token, nickName, avatarUrl, sex, extra, mobile });
};

const signIn = async ({
    token = '',
    userId = '',
}) => {
    return await RNChatSocket.signIn({ token, userId });
};

const signOut = async ({
    token = '',
}) => {
    return await RNChatSocket.signOut({ token });
};

export default {
    getVersion,
    getIMHost,
    getAppKey,
    getAppSecret,
    getDeviceID,
    getDeviceInfo,
    saveDeviceInfo,
    getUserToken,
    getUserID,
    getUserNickName,
    getUserAvatarUrl,
    saveUserToken,
    saveUserID,
    saveUserNickName,
    saveUserAvatarUrl,
    sendMessage,
    sendChatEvent,
    setMessageRead,
    getMessage,
    getContactList,
    syncMessageList,
    delMessage,
    registerDevice,
    registerUser,
    signIn,
    signOut,
};
