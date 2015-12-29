# PKNet
Android动态代理生成网络请求执行类

动态代理生成网执行器，方Android开发处理网络交互

使用：
1. 根据后台的接口协议写网络请求接口
@URL("http://*************.do")
public interface TestApi {

    @Config(type = Type.POST, supportCache = true)
    void testRequest(@Param("username") String u, @Param("password") String p, IResult<Entity> result);

}

2.将接口生成动态代理类
TestApi api = HttpManager.getProxy(TestApi.class);

3.在主线程中直接使用
api.testRequest("wzj", "123", new IResult<Entity>() {
                    @Override
                    public void onResult(Result<Entity> result) {
                        text.setText(result + "");
                    }
                });
    
                
注：
对于IExecuteHandler，可以设计自己的执行器，默认提供的DefaultExecuteHandler
    /**
     * 设置执行器
     **/
    public final HttpProxyFactory setExecuteHandler(IExecuteHandler mExecuteHandler) {
        this.mExecuteHandler = mExecuteHandler;
        return this;
    }
    
    
    DefaultExecuteHandler中可以设置缓存IHttpCache
    /**
     * 设置网络缓存插件
     **/
    public static final void setHttpCache(IHttpCache cache) {
        DefaultExecuteHandler.cache = cache;
    }
    
