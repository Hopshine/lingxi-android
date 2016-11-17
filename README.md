# 关于lingci

hobby project  

个人兴趣项目，此代码为客户端代码


## lingci简介

- 依托于社交群组，好友向社交圈，供用户吐槽交流

- 用户注册登陆

- 个人信息修改

- 动态发布点赞评论

- 单聊聊天室

- Toast彩蛋



### Toast代码演示片段


``` java
public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, "(ಥ _ ಥ)你难道要再按一次离开我么");
                mExitTime = System.currentTimeMillis();
            } else {
                int x = (int) (Math.random() * 10) + 1;
                if (exit.equals("MM")) {
                    if(x==10){
                        MoeToast.makeText(this, "恭喜你找到隐藏的偶，Game over!");
                        finish();
                    }else {
                        MoeToast.makeText(this, "你果然想要离开我(＠￣ー￣＠)");
                    }
                    mExitTime = System.currentTimeMillis();
                    exit="mm";
                } else if (exit.equals("mm")){
                    exit="MM";
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

```

## 反馈与建议
- 微博：[扶疏与谁言](http://weibo.com/374845241)
    

感谢阅读这份文档。
