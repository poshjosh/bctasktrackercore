
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 22, 2017 10:33:53 PM
 */
public class Main {

    public static void main(String [] args) throws Exception{
        final String first = "META-INF/bctasktracker";
        final String [] more = {"configs"};
        
        final Path path = Paths.get(first, more);
System.out.println("Path: "+path);
System.out.println("    : "+path.toUri());
        final URI uri = URI.create(first);
System.out.println(" URI: "+uri);        
        final URL url = new URL(uri.toURL(), more[0]);
System.out.println(" URL: "+url);
        
    }
}
