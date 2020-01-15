# 蔡徐坤大战吴亦凡小游戏-客户端

东北大学软件学院《Linux操作系统》大作业——Linux C编程实现网络游戏服务器   
服务端项目地址：https://github.com/qiurungeng/PK-Game-Server

#### 客户端介绍：

客户端采用Java Swing进行开发，大致逻辑如下：

1. 主界面为Java Swing 的Frame窗体，其中内容为一个画布类GamePanel。GamePanel属性中存有本机英雄myHero:Hero，及存有联网游戏中的所有英雄的列表：heroes:List<Hero>

2. Hero类中存有对GamePanel的引用。Hero里有Swing的键盘监听适配器，客户端开启本机本英雄的键盘监听。Hero中存有状态变量(boolean):up、down、left、right、attack，分别对应着英雄的移动方向及攻击与否。按下方位键和攻击键时，英雄将作出移动或攻击的动作。

3. 每个对象只有在调用action()方法后才能开始动作。该方法启动一个动作线程（移动动作线程、主动作线程），根据当前英雄各状态变量值的情况做移动或攻击动作，并移动位置。该动作线程的原理为：无线循环——由当前状态值决定英雄的背景图片（即英雄图像），并移动一定距离，然后线程休眠一个时间片，继续循环。游戏达到动画效果。

4. 本地英雄列表中的其他英雄状态改变靠得到服务端传送的数据包进行解析实现。

5. 攻击动作由新的攻击动作线程执行，当移动线程检测到用户状态为攻击时，开启动作线程，且不能再采取任何位置及动作变换，直到状态不为攻击。

6. 英雄状态为被击中时，所有其他动作失效，英雄图片替换为被打图片。

7. 英雄状态为死亡时，动作线程全都结束，英雄图片替换为死亡图片。

#### 游戏运行截图：

![image](https://github.com/qiurungeng/PK-Game-Client/blob/master/img/1.png)

![image](https://github.com/qiurungeng/PK-Game-Client/blob/master/img/2.png)

![image](https://github.com/qiurungeng/PK-Game-Client/blob/master/img/3.png)