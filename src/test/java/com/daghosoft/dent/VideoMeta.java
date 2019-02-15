package com.daghosoft.dent;

import java.io.File;
import java.util.Map;

import org.jcodec.containers.mp4.boxes.MetaValue;
import org.jcodec.movtool.MetadataEditor;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoMeta {

    private static final String FILE_PATH = "C:\\temp\\SampleVideo_1280x720_1mb.mkv";

    @Test
    public void mainTest() {
        File f = new File(FILE_PATH);
        System.out.println("-- >" + f.exists());

        // try {
        // DPXReader dpx = DPXReader.readFile(f);
        // // TODO remove
        // LOGGER.info("### [{}]", dpx.parseMetadata().getTimecodeString());
        // LOGGER.info("### [{}]", dpx.parseMetadata().television.frameRate);
        //
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        System.out.println("-- >" + f.exists());

        try {
            System.out.println("in");
            MetadataEditor mt = MetadataEditor.createFrom(f);
            System.out.println("2");
            Map<String, MetaValue> map = mt.getKeyedMeta();
            System.out.println(map.size());
            for (String s : map.keySet()) {
                System.out.println(s);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
