说明:
http://192.168.200.131:8848/
为自己的windows电脑NAT模式vmware安装centOS固定ip的虚拟机
nacos版本为2, 采用了服务发现, 负载均衡和配置中心

1.登录
post
http://localhost:51601/api/user/v1/login/login_auth
{
"password": "123456",
"phone": "17702142270"
}
获取token


2.领取奖励
post
http://localhost:51601/api/rewards/claim
head需要携带第一个步骤返回的token


3.

