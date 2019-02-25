package com.circuscharlie;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class CircusCharlie extends ApplicationAdapter {
	//gerekli değişkenler
	private SpriteBatch oyunSayfasi;
	private OrthographicCamera hareketliKamera;
	private Texture bgResmi;
	private Animation charlie;
	private Vector2 charliePozisyonu;
	private float gecenZaman=0;
	private Texture charlieFrame1, charlieFrame2, charlieFrame3;
	//Charlie'nin x ve y konumları
	private static final float CHARLİE_BASLANGİC_X_KONUM=10;
	private static final float CHARLİE_BASLANGİC_Y_KONUM=100;


	private enum OyunDurumu{Start, Running, GameOver} //İçerisine konulan değerleri tutmayı sağlar. Oyun durumlarını bu değişkende tutuyoruz.
	private OyunDurumu oyunDurumu= OyunDurumu.Start; // OyunDurumu değişkenini oluşturup başlangıç değerine Start atıyoruz.
	private Vector2 yercekimi=new Vector2();// x ve y değerlerini tutuyor
	private Vector2 charlieYercekimi=new Vector2();
	private static final float CHARLİE_ZIPLAMASI=500; //Karakterin zıplama yüksekliği
	private static final float CHARLİE_HIZ_X=100; //Karakterin hızı
	private static final float YERCEKIMI=-20;//Sürekli aşşağı düşecek

    private TextureRegion cemberResmi, atesResmi ; // Çağıracağımız resimler için oluşturuyoruz.

	/* Çemberleri  ve ateşleri nesne olarak kullandığımız için bir sınıf içerisinde oluşturuyoruz ve bunları oluşturduğumuz sınıftan çağırıyoruz */
    private Array<Cember> cemberler=new Array<Cember>();
    private Array<Ates> atesler=new Array<Ates>();

	private  TextureRegion readyResim, gameOverResim,puanResim;
	private Sound carpma;
	// Sanal çerçevenin oluşturulduğu kısım
	private Rectangle charlieCerceve= new Rectangle();
	private Rectangle cemberCerceve=new Rectangle ();
	private Rectangle cemberCerceve2=new Rectangle ();
	private Rectangle atesCerceve=new Rectangle();
	private OrthographicCamera arayuzKamera; //Ready, Gameover, puan logolarının gösterildiği kamera


	private ShapeRenderer shapeRenderer; //Çerceveleri görüntülemek için
	private BitmapFont font; //puanı ekrana basacakolan fontu tanımladık
	private  int puan=0; // Oyunumuz da kullanacağımız puan için oluşturduğumuz değişken. İlk değerine 0 atadık.
	private Music music;




	@Override
	public void create () { // Bütün Nesnelerimizin oluşturulduğu metot
		oyunSayfasi =new SpriteBatch();

		hareketliKamera= new OrthographicCamera();
		hareketliKamera.setToOrtho(false,800,480);

		bgResmi=new Texture("background.jpg");
        // Frame'lerimizi oluşturuyoruz
		charlieFrame1=new Texture("circus1.png");
		charlieFrame2=new Texture("circus2.png");
		charlieFrame3=new Texture("circus3.png");
		// Charlie sürekli koşuyormuş gibi efekti veren animasyonu ayarlıyoruz. İlk değer kaç sn.'de frame'ler arası geçiş yapacağını gösteriyor.
		charlie=new Animation(0.05f, new TextureRegion(charlieFrame1), new TextureRegion(charlieFrame2), new TextureRegion(charlieFrame3));
		//Charlie animasyonunu çağırıyoruz.
		charlie.setPlayMode(Animation.PlayMode.LOOP);

		charliePozisyonu=new Vector2();

        cemberResmi=new TextureRegion(new Texture("cember.png"));
        atesResmi= new TextureRegion(new Texture("ates.png"));

		arayuzKamera=new OrthographicCamera();
		arayuzKamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//uygulama penceresinin boyutlarını alıyoruz
		arayuzKamera.update();

		readyResim=new TextureRegion(new Texture("ready.png"));
		gameOverResim=new TextureRegion(new Texture("gameover.png"));
		carpma=Gdx.audio.newSound(Gdx.files.internal("carpmaa.wav"));


		shapeRenderer=new ShapeRenderer();
		puanResim=new TextureRegion(new Texture("puan.png"));
		font= new BitmapFont();
		music=Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setLooping(true);


		arkaplaniResetle();

	}

	private void arkaplaniResetle() { // Her Gameover olunduğunda geri çağrılacak metot

		charliePozisyonu.set(CHARLİE_BASLANGİC_X_KONUM, CHARLİE_BASLANGİC_Y_KONUM);
		hareketliKamera.position.x=50;//oyunun başında durması gereken pixel
		yercekimi.set(0,YERCEKIMI); //yerçekiminin ilk değerini 0'a YERCEKIMI olarak ayarladık.
		charlieYercekimi.set(0,0); //charlie yerçekimini 0 olarak ayarladık.


        cemberler.clear();//baska yerde cağrılırsa temizlesin
        atesler.clear();
		//Rastgele ates üretip bunları diziye atıyoruz
        for(int i=0;i<3;i++) {

			atesler.add(new Ates(200 + i * 500, 10, atesResmi));
		}
		//Cemberlerimizin üretildiği kısım
		cemberler.add(new Cember(80,100, cemberResmi));
		cemberler.add(new Cember(280,150, cemberResmi));
		cemberler.add(new Cember(480,230, cemberResmi));


	}

	@Override
	public void render () { /*Sayfa sayfa oyunumuzun güncellendiği metot*/

        //Varsayılan arka plan rengini tanımlıyoruz
		Gdx.gl.glClearColor(1, 0, 0, 1);
		// Karekterimiz bir yerden bir yere giderken ilk frame'deki görüntünün
		// silinmesi gerekiyorki ikinci görüntü oluşsun bu satır ise onun silinmesini sağlar.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        arkaplaniGuncelle();
		arkaplaniCizdir();
	}

	private void arkaplaniGuncelle() { // Oyunumuzdaki işlemlerin gerçekleştiği kısım
		//oyunun frame değişimleri arasındaki geçen zaman
		float deltaTime = Gdx.graphics.getDeltaTime();

		gecenZaman += deltaTime;

		//Ekrana dokunulduğunda yapılan işlemler
		if (Gdx.input.justTouched()) {
			if (oyunDurumu == OyunDurumu.Start) { // Eğer oyunDurumu Start ise oyunDurumunu Running'e eşitle ve müziği çal
				oyunDurumu = OyunDurumu.Running;
				music.play();
			}

			if (oyunDurumu == OyunDurumu.Running) { //Eğer oyunDurumu Running ise Charlie'nin yercekiminin x ve y değerlerini ata.
				charlieYercekimi.set(CHARLİE_HIZ_X, CHARLİE_ZIPLAMASI);
			}

			if (oyunDurumu == OyunDurumu.GameOver) { // oyunDurumu GameOver'a eşit ise tıklandığında Start'a eşitle. Arka Planı resetle.

				oyunDurumu = OyunDurumu.Start;

				arkaplaniResetle();
			}
		}

		if (oyunDurumu != OyunDurumu.Start) {
			charlieYercekimi.add(yercekimi);// arkaplani güncellediğin sürece oyundurumu yercekimini charlieyercekimine daima ekle yani charlie daima aşşağı düşsün

		}

		charliePozisyonu.mulAdd(charlieYercekimi, deltaTime);//yukarıda add ettiğimiz charlie yercekimini charlienin pozisyonuna ekliyoruz.

		//Kameramızın karakterimizi takip etmesi için
		hareketliKamera.position.x = charliePozisyonu.x + 400;

		System.out.println("charlie pozisyonu x:" + charliePozisyonu.x); // Ekranda charlie'nin pozisyonunu kontrol etmemiz için yazdığımız satır

		//charlienin cercevesini charlieye sabitliyoruz ilk 2 parametre charlienin boyutları son 2 parametre cercevenin boyutu
		charlieCerceve.set(charliePozisyonu.x, charliePozisyonu.y, charlieFrame1.getWidth() - 15, charlieFrame1.getHeight() - 10);


		//cember pozisyonlarını gunceller
		for (Cember cember : cemberler) {

			//cemberResmi çemberin alt kısmı için cemberResmi2 ise çemberin üst kısmı için cerceve yerlestirir.
			cemberCerceve.set(cember.pozisyon.x + (cember.resim.getRegionWidth()) / 2 + 20, (cember.pozisyon.y)-40, 10, cember.resim.getRegionHeight() / 2 - 40);
			cemberCerceve2.set(cember.pozisyon.x + (cember.resim.getRegionWidth()) / 2 + 20, cember.pozisyon.y+235, 10, cember.resim.getRegionHeight() / 2 - 40);

			//gecilen cemberleri ileriye atar
			if (hareketliKamera.position.x - cember.pozisyon.x > 400 + cember.resim.getRegionWidth()) {

				//Çemberlerin x ve y konumları
				cember.pozisyon.x += 5 * 150; //cemberin x pozisyonunu 5*150 kadar ileri atar.
				cember.pozisyon.y = (MathUtils.random(0 + 200)); // çemberin y konumlarının 0 ile 200 arasında rastgele gelmesini sağlar.
				cember.resim = this.cemberResmi;
				cember.gecildi = false; //cemberin geçilip geçilmediğini kontrol ediyoruz.
			}
			for (Ates ates : atesler) {

				//atesResmi cercevesiniyerlestirir
				atesCerceve.set(ates.pozisyon.x,ates.pozisyon.y,atesResmi.getRegionWidth()-5,atesResmi.getRegionHeight()-5);

				//gecilen atesleri ileriye atar
				if (hareketliKamera.position.x - ates.pozisyon.x > 400 + ates.resim.getRegionWidth()) {
					//Ates kazının x,y pozisyonları
					ates.pozisyon.x += 5 * 150; //ates kazanının x pozisyonunu 5*150 kadar ileri atar.
					ates.pozisyon.y = 0;
					ates.resim = this.atesResmi;
				}
				//charlie cembere ve atese carptıysa gameover olur. Overlaps rectangle'ların birbirine carpmasını kontrol eden bir metotdur.
				if (charlieCerceve.overlaps(cemberCerceve) || charlieCerceve.overlaps(cemberCerceve2) || charlieCerceve.overlaps(atesCerceve)) {

					if (oyunDurumu != OyunDurumu.GameOver) {

						carpma.play();
					}
                    // oyunDurumu GameOver olduğunda yerçekimini sıfırlar
					oyunDurumu = OyunDurumu.GameOver;
					charlieYercekimi.x = 0;
				}
				//cember gecildiyse puanı 10 arttır
				if (cember.pozisyon.x < charliePozisyonu.x && !cember.gecildi) {
					puan += 10;
					cember.gecildi = true;

				}

			}

		}
	}

	private void arkaplaniCizdir() { // Arka planın çizdirildiği metot (Render metotuna yardımcı olması için oluşturduk)
		hareketliKamera.update();

		//oyun sayfasını hareketli kameraya ayarlıyoruz
		oyunSayfasi.setProjectionMatrix(hareketliKamera.combined);

		oyunSayfasi.begin(); // oyun sayfasını başlatır

		oyunSayfasi.draw(bgResmi,hareketliKamera.position.x- bgResmi.getWidth()/2,0); //kamerayı takip eden bgresmi (Arka plan resmi)

        // Çemberlerin sonsuza kadar oluşturulmasını sağlar.
        for(Cember cember: cemberler){
            oyunSayfasi.draw(cember.resim, cember.pozisyon.x,cember.pozisyon.y);

        }
		// Ateslerin sonsuza kadar oluşturulmasını sağlar.
		for(Ates ates: atesler){
			oyunSayfasi.draw(ates.resim, ates.pozisyon.x,ates.pozisyon.y);

		}

		//Charlie animasyonunu çizdirdiğimiz kısım. İlk değer Charlie'nin o anki frame'ini alıyor.
		oyunSayfasi.draw((TextureRegion) charlie.getKeyFrame(gecenZaman), charliePozisyonu.x, charliePozisyonu.y);

        oyunSayfasi.end(); // oyun sayfasını bitirir

		//oyun sayfasını araüz kameraya ayarlar
		oyunSayfasi.setProjectionMatrix(arayuzKamera.combined);

		oyunSayfasi.begin();
        // oyunDurumu start ise ready resmi çizdirilecek.
		if(oyunDurumu == OyunDurumu.Start){
			oyunSayfasi.draw(readyResim, Gdx.graphics.getWidth() /2 - readyResim.getRegionWidth()/ 2, Gdx.graphics.getHeight()/2-readyResim.getRegionHeight()/2 );

		}
		// oyunDurumu gameover ise gameover resmi çizdirilecek, puan sıfırlanacak ve aynı zamanda müzik durdurulacak
		if(oyunDurumu == OyunDurumu.GameOver){
			puan=0;
			music.pause();
			oyunSayfasi.draw(gameOverResim,Gdx.graphics.getWidth() /2 - gameOverResim.getRegionWidth()/ 2, Gdx.graphics.getHeight()/2-gameOverResim.getRegionHeight()/2 );
		}
		//oyunDurumu GameOver veya Running ise puan kısmı görüntülenecek
		if(oyunDurumu==OyunDurumu.GameOver || oyunDurumu==OyunDurumu.Running){
			oyunSayfasi.draw(puanResim,960,610);
			font.draw(oyunSayfasi,""+puan, 980, 660 );

		}

		oyunSayfasi.end(); // Oyun sayfasını bitirir.

	}

}
