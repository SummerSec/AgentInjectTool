<h1 align="center" >AgentInjectTool</h1>
<h3 align="center" >使用Agent技术，集成实战中所需要的小功能。</h3>
 <p align="center">
    <a href="https://github.com/SummerSec/AgentInjectTool"></a>
    <a href="https://github.com/SummerSec/AgentInjectTool"><img alt="AgentInjectTool" src="https://img.shields.io/badge/AgentInjectTool-green"></a>
    <a href="https://github.com/SummerSec/AgentInjectTool"><img alt="Forks" src="https://img.shields.io/github/forks/SummerSec/AgentInjectTool"></a>
     <a href="https://github.com/SummerSec/AgentInjectTool"><img alt="Release" src="https://img.shields.io/github/release/SummerSec/AgentInjectTool.svg"></a>
  <a href="https://github.com/SummerSec/AgentInjectTool"><img alt="Stars" src="https://img.shields.io/github/stars/SummerSec/AgentInjectTool.svg?style=social&label=Stars"></a>
     <a href="https://github.com/SummerSec"><img alt="Follower" src="https://img.shields.io/github/followers/SummerSec.svg?style=social&label=Follow"></a>
     <a href="https://github.com/SummerSec"><img alt="Visitor" src="https://visitor-badge.laobi.icu/badge?page_id=SummerSec.AgentInjectTool"></a>
	<a href="https://twitter.com/SecSummers"><img alt="SecSummers" src="https://img.shields.io/twitter/follow/SecSummers.svg"></a>
	<a xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="https://visitor-badge.laobi.icu"><rect fill="rgba(0,0,0,0)" height="20" width="49.6"/></a>
	<a xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="https://visitor-badge.laobi.icu"><rect fill="rgba(0,0,0,0)" height="20" width="17.0" x="49.6"/></a>
	</p>



## 📝 TODO







工具实现参考博客文章（待上传：）

---
## 🐉 来龙去脉



在红队行动中经常会遇到拿到Webshell后找不到数据库密码存放位置或者是数据库密码被加密的情况(需要逆向代码查找解密逻辑)。

为什么要获取shiro的key？

1. 可以方便我们快速的实现内网横向，毕竟shiro这个漏洞利用已经非常非常成熟了。
2. 可以将这个key加入我们key字典中，方便之后的项目中测试。
3. 如果我们修改key，但我们一失手忘记掉了key，也还要补救的措施。
4. 如果点掉了，可以通过shiro这个入口快速重新切进去。

修改key使用Agent技术，能够达到通用且方便的目的。





---
## ⚡下载安装

* [https://github.com/SummerSec/AgentInjectTool/releases](https://github.com/SummerSec/AgentInjectTool/releases)



---
## 🎬 使用方法

本地环境测试DEMO（建议使用JDK8以下启动）

1. 首先可以确定环境的key是默认的，并且是可以执行命令的。

![image-20220308224448662](https://cdn.jsdelivr.net/gh/SummerSec/Images/48u4448ec48u4448ec.png)

![image-20220308224621598](https://cdn.jsdelivr.net/gh/SummerSec/Images/21u4621ec21u4621ec.png)

2. 执行命令`java -jar AgentInjectTool.jar list`，获取环境启动的pid。

![image-20220308224738899](https://cdn.jsdelivr.net/gh/SummerSec/Images/39u4739ec39u4739ec.png)

3. 执行命令`java -jar AgentInjectTool.jar inject {pid} {file.txt|shirokey}`

> java -jar AgentInjectTool.jar inject 96864 G:/temp/temp.txt
>
> // 注意一定得使用反斜杠<font color=red>/</font> 

![image-20220308225003956](https://cdn.jsdelivr.net/gh/SummerSec/Images/14u5014ec14u5014ec.png)

> 触发获取key操作，需要我们手动发送请求登录请求，无论正确与否均可。比例说使用工具的**检测当前密钥**功能

![image-20220308232335062](https://cdn.jsdelivr.net/gh/SummerSec/Images/35u2335ec35u2335ec.png)

> java -jar AgentInjectTool.jar inject  96864  ES2ZK5q7qgNrkigR4EmGNg==

![image-20220308232433218](https://cdn.jsdelivr.net/gh/SummerSec/Images/33u2433ec33u2433ec.png)

![image-20220308232505324](https://cdn.jsdelivr.net/gh/SummerSec/Images/5u255ec5u255ec.png)

> 使用获取key功能

![image-20220308232609815](https://cdn.jsdelivr.net/gh/SummerSec/Images/9u269ec9u269ec.png)






## 🅱️ 免责声明

该工具仅用于安全自查检测

由于传播、利用此工具所提供的信息而造成的任何直接或者间接的后果及损失，均由使用者本人负责，作者不为此承担任何责任。

本人拥有对此工具的修改和解释权。未经网络安全部门及相关部门允许，不得善自使用本工具进行任何攻击活动，不得以任何方式将其用于商业目的。

该工具只授权于企业内部进行问题排查，请勿用于非法用途，请遵守网络安全法，否则后果作者概不负责

----

![as](https://starchart.cc/SummerSec/AgentInjectTool.svg)
