package com.sun.pojo;

import jakarta.persistence.*;
/**
 * 表名：fans
 * 表注释：粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。
 *         关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。
 *         字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。
*/
public class Fans {
    @Id
    private String id;

    /**
     * 作家用户id
     */
    @Column(name = "writer_id")
    private String writerId;

    /**
     * 粉丝用户id
     */
    @Column(name = "fan_id")
    private String fanId;

    /**
     * 粉丝头像
     */
    private String face;

    /**
     * 粉丝昵称
     */
    @Column(name = "fan_nickname")
    private String fanNickname;

    /**
     * 粉丝性别
     */
    private Integer sex;

    /**
     * 省份
     */
    private String province;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取作家用户id
     *
     * @return writerId - 作家用户id
     */
    public String getWriterId() {
        return writerId;
    }

    /**
     * 设置作家用户id
     *
     * @param writerId 作家用户id
     */
    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    /**
     * 获取粉丝用户id
     *
     * @return fanId - 粉丝用户id
     */
    public String getFanId() {
        return fanId;
    }

    /**
     * 设置粉丝用户id
     *
     * @param fanId 粉丝用户id
     */
    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    /**
     * 获取粉丝头像
     *
     * @return face - 粉丝头像
     */
    public String getFace() {
        return face;
    }

    /**
     * 设置粉丝头像
     *
     * @param face 粉丝头像
     */
    public void setFace(String face) {
        this.face = face;
    }

    /**
     * 获取粉丝昵称
     *
     * @return fanNickname - 粉丝昵称
     */
    public String getFanNickname() {
        return fanNickname;
    }

    /**
     * 设置粉丝昵称
     *
     * @param fanNickname 粉丝昵称
     */
    public void setFanNickname(String fanNickname) {
        this.fanNickname = fanNickname;
    }

    /**
     * 获取粉丝性别
     *
     * @return sex - 粉丝性别
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * 设置粉丝性别
     *
     * @param sex 粉丝性别
     */
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    /**
     * 获取省份
     *
     * @return province - 省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置省份
     *
     * @param province 省份
     */
    public void setProvince(String province) {
        this.province = province;
    }
}