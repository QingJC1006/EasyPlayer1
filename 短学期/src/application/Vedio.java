package application;

import java.io.File;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.security.x509.Extension;

public class Vedio extends Application{

	private static final String Media_URL0="./src/Pic/梵高.mp4";
//	private static final String Media_URL1="./src/Pic/进击的巨人.mp4";
//	private static final String Media_URL2="./src/Pic/新海城.mp4";
//	private static final String Media_URL3="./src/Pic/埃尔文.mp4";
	private String Media_URL;
	private Button playBut=new Button(">");
	private Button rewindBut=new Button("<<");
	private Button chooseBut=new Button("选择文件");
	private Slider volSlider;	//音量控制
	private Slider ProSlider;	//进度控制
	private HBox hpane1,hpane2;
	private VBox vpane=new VBox();
	private BorderPane mainpane=new BorderPane();

	private Label time=new Label();
	private double all_time = 0;

	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;

	public static String formattime(double this_time,double all_time,int type){
		String thistime = String.format("%02d:%02d:%02d",(int)this_time/3600,(int)this_time%3600/60,(int)this_time%60);
		String alltime = String.format("%02d:%02d:%02d",(int)all_time/3600,(int)all_time%3600/60,(int)all_time%60);
		return type==1?thistime:type==2?alltime:thistime+"/"+alltime;
	}

	public static void main(String[] args) {
		launch();
	}

	public void start(Stage primaryStage) throws Exception {
	    // 控件初始化
	    Label vol = new Label("音量");
	    vol.setStyle("-fx-text-fill:white");

	    //音量条初始化
	    Slider volSlider = new Slider(0, 100, 60);
	    volSlider.setMaxWidth(Region.USE_PREF_SIZE);
	    volSlider.setMinWidth(30);
	    volSlider.setValue(50);
	    //进度调初始化
	    Slider ProSlider = new Slider(0, 100, 0);

	    Label time = new Label();
	    time.setStyle("-fx-text-fill:white");

	    Button chooseBut = new Button("选择文件");
	    Button playBut = new Button(">");
	    Button rewindBut = new Button("<<");

	    HBox hpane1 = new HBox(10, chooseBut, playBut, rewindBut, ProSlider, time, vol, volSlider);
	    hpane1.setStyle("-fx-background-color:#888;-fx-font-size:16px;");
	    hpane1.setPrefHeight(40);
	    hpane1.setAlignment(Pos.CENTER);

	    VBox vpane = new VBox();
	    vpane.getChildren().addAll(hpane1);
	    vpane.setAlignment(Pos.CENTER);

	    BorderPane mainpane = new BorderPane();
	    mainpane.setBottom(vpane);

	    // 文件选择事件处理
	    chooseBut.setOnAction(event -> {

	    	if (mediaPlayer != null) {
	            mediaPlayer.stop();
	            mediaPlayer.dispose();
	        }

	    	Stage stage = new Stage();

	        FileChooser fc = new FileChooser();
	        fc.setTitle("单选文件");
	        fc.setInitialDirectory(new File("D:\\JAVA\\短学期"));
	        fc.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("视频类型", "*.avi", "*.mp4"),
	                new FileChooser.ExtensionFilter("音频类型", "*.mp3")
	        );

	        File selectedFile = fc.showOpenDialog(stage);
	        if (selectedFile != null) {
	            Media media = new Media(selectedFile.toURI().toString());
	            mediaPlayer = new MediaPlayer(media);
	            mediaView = new MediaView(mediaPlayer);
	            mediaView.setFitWidth(800);
	            mediaView.setFitHeight(480);
	            mainpane.setCenter(mediaView);

	            //音量条绑定
	            mediaPlayer.volumeProperty().bind(volSlider.valueProperty().divide(100));

	            // 设置进度条绑定与控制
	            ProSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
	                if (ProSlider.isValueChanging()) {
	                    double durationMillis = mediaPlayer.getMedia().getDuration().toMillis();
	                    double seekTime = durationMillis * ProSlider.getValue() / 100.0;
	                    mediaPlayer.seek(Duration.millis(seekTime));
	                    mediaPlayer.play();
	                }
	            });
	            //同步播放时间
	            mediaPlayer.currentTimeProperty().addListener((obs, oldValue, newValue) -> {
	                if (!ProSlider.isValueChanging()) {
	                    double currentTime = mediaPlayer.getCurrentTime().toMillis() / 1000;
	                    double totalDuration = mediaPlayer.getTotalDuration().toMillis() / 1000;
	                    time.setText(formattime(currentTime, totalDuration, 0));
	                    ProSlider.setValue((currentTime / totalDuration) * 100.0);
	                }
	            });

	            // 播放按钮事件
	            playBut.setOnAction(e -> {
	                if (playBut.getText().equals(">")) {
	                    mediaPlayer.play();
	                    playBut.setText("||");
	                } else {
	                    mediaPlayer.pause();
	                    playBut.setText(">");
	                }
	            });

	            // 重播按钮事件
	            rewindBut.setOnAction(e -> {
	                mediaPlayer.seek(Duration.ZERO);
	            });

	            primaryStage.setWidth(1000);
	            primaryStage.setHeight(600);
	            primaryStage.setTitle("MV播放器");
	            primaryStage.show();
	        	}
	    });

	    Scene scene = new Scene(mainpane, 1000, 600);
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}
