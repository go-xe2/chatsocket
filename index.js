
import { NativeModules } from 'react-native';
const { RNChatsocket } = NativeModules;


/**
 * 获取chatsocket版本
 * @returns {*}
 */
const getVersion = () => {
    return RNChatsocket.getVersion();
};

/**
 * 获取IM服务配置地址
 * @returns {*}
 */
const getIMHost =  () => {
    return RNChatsocket.getIMHost();
};

/**
 * 获取设备id
 * @returns {*}
 */
const  getDeviceID = () => {
    return RNChatsocket.getDeviceID();
};

/**
 * 获取IM的appKey配置
 * @returns {*}
 */
const getAppKey = () => {
    return RNChatsocket.getAppKey();
};

/**
 * 获取IM的appSecret配置
 * @returns {*}
 */
const getAppSecret = () => {
    return RNChatsocket.getAppSecret();
};
/**
 * 获取用户令牌
 * @returns {*}
 */
const  getUserToken = () => {
    return RNChatsocket.getUserToken();
};

/**
 * 获取用户id
 * @returns {*}
 */
const getUserID = () => {
    return RNChatsocket.getUserID();
};

/**
 * 获取用户昵称
 * @returns {*}
 */
const getUserNickName = () => {
    return RNChatsocket.getUserNickName();
};

/**
 * 获取用户头像
 * @returns {*}
 */
const getUserAvatarUrl = () => {
    return RNChatsocket.getUserAvatarUrl();
};

/**
 * 保存用户令牌
 * @param token
 * @returns {Promise<*>}
 */
const saveUserToken = async (token) => {
    return await RNChatsocket.saveUserToken(token);
};

/**
 * 保存用户id
 * @param userID
 * @returns {Promise<*>}
 */
const saveUserID = async (userID) => {
    return await RNChatsocket.saveUserID(userID);
};

/**
 * 保存用户昵称
 * @param nickName
 * @returns {Promise<*>}
 */
 const  saveUserNickName = async (nickName) => {
    return await RNChatsocket.saveUserNickName(nickName);
};

/**
 * 保存用户头像
 * @param avatarUrl
 * @returns {Promise<*>}
 */
const saveUserAvatarUrl = async (avatarUrl) => {
    return await RNChatsocket.saveUserAvatarUrl(avatarUrl);
}

export default {
    getVersion,
    getIMHost,
    getAppKey,
    getAppSecret,
    getDeviceID,
    getUserToken,
    getUserID,
    getUserNickName,
    getUserAvatarUrl,
    saveUserToken,
    saveUserID,
    saveUserNickName,
    saveUserAvatarUrl,
};
