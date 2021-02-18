File basePath = new File("C:/Users/LAM/Downloads/video/¤p©ú¬P")

List<File> mp4Files = basePath.listFiles().toList().findAll{ File file -> file.getName().endsWith(".mp4") }

List cmds = mp4Files.collect{ File mp4File ->
    List cmd = ["ffmpeg", "-i", "\"" + mp4File.getName() + "\"", "-vn", "-acodec", "mp3", "\"" + mp4File.getName() + ".mp3" + "\""]
    return cmd.join(" ")
}

cmds.each{
    println it
}

File commandFile = new File(basePath, "extract_audio.bat")

commandFile.newWriter("utf8").withWriter{ selfWriter ->
    selfWriter.writeLine("chcp 65001")
    selfWriter.writeLine("")
    cmds.each{ String cmd ->
        selfWriter.writeLine(cmd)
    }
    selfWriter.writeLine("")
    selfWriter.writeLine("pause")
}

return 0