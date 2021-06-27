package MindustryBR.util;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.graphics.g2d.TextureAtlas.TextureAtlasData;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.StringMap;
import arc.util.io.CounterInputStream;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.ContentLoader;
import mindustry.core.GameState;
import mindustry.core.Version;
import mindustry.core.World;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.game.Team;
import mindustry.io.MapIO;
import mindustry.io.SaveIO;
import mindustry.io.SaveVersion;
import mindustry.world.Block;
import mindustry.world.CachedTile;
import mindustry.world.Tile;
import mindustry.world.WorldContext;
import mindustry.world.blocks.environment.OreBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import static mindustry.Vars.*;

public class ContentHandler{
    public static final String schemHeader = schematicBaseStart;

    Color co = new Color();
    Graphics2D currentGraphics;
    BufferedImage currentImage;
    ObjectMap<String, Fi> imageFiles = new ObjectMap<>();
    ObjectMap<String, BufferedImage> regions = new ObjectMap<>();

    public ContentHandler(){
        //clear cache
        new Fi("cache").deleteDirectory();

        Version.enabled = false;
        Vars.content = new ContentLoader();
        Vars.content.createBaseContent();
        for(ContentType type : ContentType.all){
            for(Content content : Vars.content.getBy(type)){
                try{
                    content.init();
                }catch(Throwable ignored){
                }
            }
        }

        String assets = "../Mindustry/core/assets/";
        Vars.state = new GameState();

        TextureAtlasData data = new TextureAtlasData(new Fi(assets + "sprites/sprites.aatls"), new Fi(assets + "sprites"), false);
        Core.atlas = new TextureAtlas();

        new Fi("../Mindustry/core/assets-raw/sprites_out").walk(f -> {
            if(f.extEquals("png")){
                imageFiles.put(f.nameWithoutExtension(), f);
            }
        });

        data.getPages().each(page -> {
            page.texture = Texture.createEmpty(null);
            page.texture.width = (int)page.width;
            page.texture.height = (int)page.height;
        });

        data.getRegions().each(reg -> {
            Core.atlas.addRegion(reg.name, new AtlasRegion(reg.page.texture, reg.left, reg.top, reg.width, reg.height){{
                name = reg.name;
                texture = reg.page.texture;
            }});
        });

        Lines.useLegacyLine = true;
        Core.atlas.setErrorRegion("error");
        Draw.scl = 1f / 4f;
        Core.batch = new SpriteBatch(0){
            @Override
            protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation){
                x += 4;
                y += 4;

                x *= 4;
                y *= 4;
                width *= 4;
                height *= 4;

                y = currentImage.getHeight() - (y + height/2f) - height/2f;

                AffineTransform at = new AffineTransform();
                at.translate(x, y);
                at.rotate(-rotation * Mathf.degRad, originX * 4, originY * 4);

                currentGraphics.setTransform(at);
                BufferedImage image = getImage(((AtlasRegion)region).name);
                if(!color.equals(Color.white)){
                    image = tint(image, color);
                }

                currentGraphics.drawImage(image, 0, 0, (int)width, (int)height, null);
            }

            @Override
            protected void draw(Texture texture, float[] spriteVertices, int offset, int count){
                //do nothing
            }
        };

        for(ContentType type : ContentType.values()){
            for(Content content : Vars.content.getBy(type)){
                try{
                    content.load();
                    //content.loadIcon();
                }catch(Throwable ignored){
                }
            }
        }

        try{
            BufferedImage image = ImageIO.read(new File("../Mindustry/core/assets/sprites/block_colors.png"));

            for(Block block : Vars.content.blocks()){
                block.mapColor.argb8888(image.getRGB(block.id, 0));
                if(block instanceof OreBlock){
                    block.mapColor.set(((OreBlock)block).itemDrop.color);
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        world = new World(){
            public Tile tile(int x, int y){
                return new Tile(x, y);
            }
        };
    }

    private BufferedImage getImage(String name){
        return regions.get(name, () -> {
            try{
                return ImageIO.read(imageFiles.get(name, imageFiles.get("error")).file());
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        });
    }

    private BufferedImage tint(BufferedImage image, Color color){
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Color tmp = new Color();
        for(int x = 0; x < copy.getWidth(); x++){
            for(int y = 0; y < copy.getHeight(); y++){
                int argb = image.getRGB(x, y);
                tmp.argb8888(argb);
                tmp.mul(color);
                copy.setRGB(x, y, tmp.argb8888());
            }
        }
        return copy;
    }

    public static Map readMap(InputStream is) throws IOException{
        try(InputStream ifs = new InflaterInputStream(is); CounterInputStream counter = new CounterInputStream(ifs); DataInputStream stream = new DataInputStream(counter)){
            Map out = new Map();

            SaveIO.readHeader(stream);
            int version = stream.readInt();
            SaveVersion ver = SaveIO.getSaveWriter(version);
            StringMap[] metaOut = {null};
            ver.region("meta", stream, counter, in -> metaOut[0] = ver.readStringMap(in));

            StringMap meta = metaOut[0];

            out.name = meta.get("name", "Unknown");
            out.author = meta.get("author");
            out.description = meta.get("description");
            out.tags = meta;

            int width = meta.getInt("width"), height = meta.getInt("height");

            var floors = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            var walls = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            var fgraphics = floors.createGraphics();
            var jcolor = new java.awt.Color(0, 0, 0, 64);
            int black = 255;
            CachedTile tile = new CachedTile(){
                @Override
                public void setBlock(Block type){
                    super.setBlock(type);

                    int c = MapIO.colorFor(block(), Blocks.air, Blocks.air, team());
                    if(c != black && c != 0){
                        walls.setRGB(x, floors.getHeight() - 1 - y, conv(c));
                        fgraphics.setColor(jcolor);
                        fgraphics.drawRect(x, floors.getHeight() - 1 - y + 1, 1, 1);
                    }
                }
            };

            ver.region("content", stream, counter, ver::readContentHeader);
            ver.region("preview_map", stream, counter, in -> ver.readMap(in, new WorldContext(){
                @Override public void resize(int width, int height){}
                @Override public boolean isGenerating(){return false;}
                @Override public void begin(){
                    world.setGenerating(true);
                }
                @Override public void end(){
                    world.setGenerating(false);
                }

                @Override
                public void onReadBuilding(){
                    //read team colors
                    if(tile.build != null){
                        int c = tile.build.team.color.argb8888();
                        int size = tile.block().size;
                        int offsetx = -(size - 1) / 2;
                        int offsety = -(size - 1) / 2;
                        for(int dx = 0; dx < size; dx++){
                            for(int dy = 0; dy < size; dy++){
                                int drawx = tile.x + dx + offsetx, drawy = tile.y + dy + offsety;
                                walls.setRGB(drawx, floors.getHeight() - 1 - drawy, c);
                            }
                        }
                    }
                }

                @Override
                public Tile tile(int index){
                    tile.x = (short)(index % width);
                    tile.y = (short)(index / width);
                    return tile;
                }

                @Override
                public Tile create(int x, int y, int floorID, int overlayID, int wallID){
                    if(overlayID != 0){
                        floors.setRGB(x, floors.getHeight() - 1 - y, conv(MapIO.colorFor(Blocks.air, Blocks.air, content.block(overlayID), Team.derelict)));
                    }else{
                        floors.setRGB(x, floors.getHeight() - 1 - y, conv(MapIO.colorFor(Blocks.air, content.block(floorID), Blocks.air, Team.derelict)));
                    }
                    return tile;
                }
            }));

            fgraphics.drawImage(walls, 0, 0, null);
            fgraphics.dispose();

            out.image = floors;

            return out;

        }finally{
            content.setTemporaryMapper(null);
        }
    }

    static int conv(int rgba){
        return new Color().set(rgba).argb8888();
    }

    public static class Map{
        public String name, author, description;
        public ObjectMap<String, String> tags = new ObjectMap<>();
        public BufferedImage image;
    }
}