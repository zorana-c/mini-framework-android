package com.mall.ui.commodity.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.framework.core.compat.UIRes;
import com.framework.core.ui.abs.UIViewHolder;
import com.mall.R;
import com.mall.bean.Commodity;

import java.util.List;

/**
 * @Author create by Zhengzelong on 2024-03-26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommodityWebViewHolder<T extends Commodity> extends UIViewHolder<T> {
    @NonNull
    public static <T extends Commodity> CommodityWebViewHolder<T> create(@NonNull LayoutInflater inflater,
                                                                         @NonNull ViewGroup parent) {
        final View itemView;
        itemView = inflater.inflate(R.layout.item_commodity_web_layout, parent, false);
        return new CommodityWebViewHolder<>(itemView);
    }

    @NonNull
    private final WebView infoWebView;

    public CommodityWebViewHolder(@NonNull View itemView) {
        super(itemView);
        this.infoWebView = this.requireViewById(R.id.infoWebView);
    }

    @Override
    public void onInit(@NonNull List<Object> payloads) {
        super.onInit(payloads);
        final WebSettings settings = this.infoWebView.getSettings();
        settings.setUseWideViewPort(true);
        // 适应屏幕宽度
        settings.setLoadWithOverviewMode(true);
        // 设置字体大小
        settings.setDefaultFontSize(UIRes.dip2px(12));
        // 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 设置数据来源
        this.infoWebView.loadData("<p style=\"text-align:center;\">富有传奇色彩的“尚方宝剑”</p><p><br/></p><p " +
                "style=\"text-align:center;\">乾隆御用“小神锋”</p><p><br/></p><p style=\"text-align:center;\">" +
                "乾隆收藏的“顶级刀剑”</p><p><br/></p><p style=\"text-align:center;\">乾隆与老虎搏斗的“阿虎枪”</p>" +
                "<p><br/></p><p style=\"text-align:center;\">八旗盔甲、马鞍、马鞭、弓弩……</p><p><br/></p><p " +
                "style=\"text-align:center;\">150余件武备文物集中亮相嘉德艺术中心。</p><p><br/></p><p " +
                "style=\"text-align:left;\">“崇威耀德——故宫博物院藏清代武备展”是嘉德与故宫博物院第四次携手合作。此次展览，" +
                "不仅呈现了武备文物独特的艺术价值，同时也着力于讲述文物背后的历史发展脉络。</p><p><br/></p><p " +
                "style=\"text-align:left;\">此次展览的名称“崇威耀德”，出自故宫博物院藏道光皇帝骑马戎装像——《旻宁耀德崇威》。" +
                "展览名称将四个字颠倒“一国威严在德不在威”。</p><p><br/></p><p style=\"text-align:center;\">" +
                "<img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10." +
                "artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2Fbcd0b0f064b690382786a67c566d920c.jpg&amp;w=" +
                "600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/bcd0b0f064b690382786a67c566d920c.jpg" +
                "\"/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;" +
                "h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F71fc370af780e717f37" +
                "ede99a4a9a207.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/71fc370af780e7" +
                "17f37ede99a4a9a207.jpg\"/></p><p><br/></p><p style=\"text-align:center;\">展览现场</p><p><br/></p>" +
                "<p style=\"text-align:left;\">武备作为故宫博物院藏品中的重要门类，藏品数以万计，此次展览甄选了一百五十余件故宫珍" +
                "藏的武备文物，以清代御制藏品为主，很多都是平时难得一见的宫廷珍贵文物。<br/></p><p><br/></p>" +
                "<p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;" +
                "src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F6a94f4bebbcc15d72bbc1ea93d802c39" +
                ".jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/6a94f4bebbcc15d72bbc1ea93d802c39" +
                ".jpg\"/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;" +
                "h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2Fbb26d05846218c183e2c" +
                "70d430aeed00.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/bb26d05846218" +
                "c183e2c70d430aeed00.jpg\"/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.ne" +
                "t/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F" +
                "e5f3d3e9ed3f1893ed92c74eb367d1a7.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/2" +
                "02207/e5f3d3e9ed3f1893ed92c74eb367d1a7.jpg\"/></p><p style=\"text-align:center;\"><br/></p><p style" +
                "=\"text-align:left;\">嘉德艺术中心用当代手法展现历史文物，讲述文物背后的故事，为观者打造了一场身临其境的观展体验，让" +
                "观者能够在不同单元的场景中体会不一样的观展氛围。<br/></p><p><br/></p><p style=\"text-align:left;\">进入展厅，各" +
                "种精美的武备文物，使人眼花缭乱。一件件文物，为我们讲述着背后的文化与历史。</p><p><br/></p><p style=\"text-align:c" +
                "enter;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artim" +
                "g.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F19fb48bdd4373a250de08cea31108c33.jpg&amp;w=600\" alt=\"https" +
                "://img10.artimg.net/public/beian/jpg/202207/19fb48bdd4373a250de08cea31108c33.jpg\"/></p><p style=\"t" +
                "ext-align:center;\"><br/></p><p style=\"text-align:left;\">当你站在“阅武”二字下方，左右两边陈列着八旗盔甲，耳边" +
                "慢慢响起低沉厚重的背景音乐，一种皇家的威严扑面而来。有那么一瞬间，好像自己真的穿越到了清朝。</p><p><br/></p><p sty" +
                "le=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%" +
                "2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F264df266e41c7096a862d0bbf56bce90.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/264df266e41c7096a862d0bbf56bce90.jpg\"/></p><p style=\"text-align:center;\"><br/></p><p style=\"text-align:center;\">“武能安邦，文可兴国”。<br/></p><p><br/></p><p style=\"text-align:left;\">在以骑射开国的清朝，武备尤为重要。武备的种类不仅局限于刀剑，它的用途也不仅局限于战争。武备还包括了盔甲、弓矢、刀剑、马具等诸多类别，是宫廷生活的缩影。</p><p><br/></p><p style=\"text-align:left;\">此次展览根据武备文物的不同用途，以“礼遇天地”、“神锋握胜”、“宝冶凝涛”三个单元呈现。让我们一同置身武备，领略武备带来的独特魅力。</p><p style=\"text-align:center;\"><br/></p><p style=\"text-align:left;\">古人将“礼”划分为“五礼”，分别是“吉礼”、“军礼”、“凶礼”、“宾礼”、“嘉礼”。清代对“五礼”的继承和发展，在武备上也有体现。</p><p><br/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F549873b00b3317653a67a830e61dc36c.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/549873b00b3317653a67a830e61dc36c.jpg\"/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F4d01fd4c19e4fdf5d6f67060d693fb0f.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/4d01fd4c19e4fdf5d6f67060d693fb0f.jpg\"/></p><p style=\"text-align:center;\"><br/></p><p style=\"text-align:left;\">这一单元展出的藏品则以“礼”为主线，选取了“五礼”中的“吉礼”和“军礼”的藏品进行展示，让观者们感受皇家祭祀的隆重、狩猎的紧张、阅兵的威严。<br/></p><p><br/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2Fe0c8df3ca0fedad3b07eb3011829e116.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/e0c8df3ca0fedad3b07eb3011829e116.jpg\"/></p><p style=\"text-align:center;\"><br/></p><p style=\"text-align:left;\">“吉礼”在“五礼”中等级最高，皇帝在祭祀时会佩戴相应的“神锋”腰刀，携带吉礼专用的楗，箭矢的种类和数量也都有着明确规定。可惜这把“神锋”腰刀目前在法国巴黎军事博物馆，是英法联军攻入北京时，将其从圆明园掠走了。<br/></p><p><br/></p><p style=\"text-align:left;\">我们今天在展览现场看到的是乾隆皇帝御用“小神锋”，这是它在修复后首次走出故宫展出。在唐晏著《天咫偶闻》中，就曾提过乾隆皇帝这柄“小神锋”。乾隆皇帝非常喜爱，出行时由侍卫携带，平日会放置在御座旁，经常把玩。</p><p><br/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2Fbf95a6f115ab8f959c0e1010f41dc0cd.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/bf95a6f115ab8f959c0e1010f41dc0cd.jpg\"/></p><p style=\"text-align:center;\"><br/></p><p style=\"text-align:left;\">“小神锋”是乾隆皇帝命造办处，仿照“神锋”打造而成，除了刀身的长度与宝石镶嵌不同，两柄刀几乎没什么区别。清代的腰刀大部分是弯刀，而吉礼的随侍佩刀为直刀。这种直刀并不是传统的中原刀剑，其原型是金川藏刀。<br/></p><p><br/></p><p style=\"text-align:left;\">乾隆十四年，朝廷第一次取得了金川之战的胜利，这场战争的胜利来之不易。乾隆帝为了纪念这场战争，为了表达对上天的感恩，打造了这柄刀。乾隆命铸造者将刀尖改为剑尖，并在背衔镶嵌了一条金龙，护手改为八楞形。</p><p><br/></p><p style=\"text-align:left;\">满族人善弓马骑射，以武功定天下。在吉礼祭祀时也会用到弓箭。</p><p><br/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2Fcbfad755d172613eb3dfedbf306fd3cf.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/cbfad755d172613eb3dfedbf306fd3cf.jpg\"/></p><p style=\"text-align:center;\"><img src=\"https://thumb.artron.net/Img/image?c=0&amp;h=0&amp;src=https%3A%2F%2Fimg10.artimg.net%2Fpublic%2Fbeian%2Fjpg%2F202207%2F2a9aa933711e3a23d8ae48acc9fa9e5a.jpg&amp;w=600\" alt=\"https://img10.artimg.net/public/beian/jpg/202207/2a9aa933711e3a23d8ae48acc9fa9e5a.jpg\"/></p>", "text/html", null);
    }
}
