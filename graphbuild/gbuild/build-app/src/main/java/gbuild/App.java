package gbuild;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.datalab.api.GraphElementsController;
import org.gephi.datalab.spi.rows.merge.AttributeRowsMergeStrategy;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.DependantColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;
import java.sql.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

class App{
    static final String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    public static void main(String[] args) throws SQLException {
        try {
            //System.out.print(args[0] + "is file chosen");
            attempt(args[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
   public static void attempt(String fileName) throws SQLException {
//Init a project - and therefore a workspace
       ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
       pc.newProject();
       Workspace workspace = pc.getCurrentWorkspace();

       GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
       ImportController importController = Lookup.getDefault().lookup(ImportController.class);
       AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
       AppearanceModel appearanceModel = appearanceController.getModel();

//Import file
       Container container;
       try {
           File file = new File(fileName);
           container = importController.importFile(file);
       } catch (Exception ex) {
           ex.printStackTrace();
           return;
       }

//Append imported data to GraphAPI
       importController.process(container, new DefaultProcessor(), workspace);

//See if graph is well imported
       DirectedGraph graph = graphModel.getDirectedGraph();
       System.out.println("Nodes: " + graph.getNodeCount());
       System.out.println("Edges: " + graph.getEdgeCount());

//       mergeIntoIPs(graph, appearanceModel, appearanceController);
//       long startTime = System.currentTimeMillis();
//       searchDbInd(graph);
//       long endTime = System.currentTimeMillis();
//       System.out.println("Individual Queries took " + (endTime - startTime)/1000 + " seconds");
//       long startTime2 = System.currentTimeMillis();
//       searchDb(graph);
//       long endTime2 = System.currentTimeMillis();
//       System.out.println("Batch Query took " + (endTime2 - startTime2)/1000 + " seconds");
//       long delta = (-1 * ((endTime2 - startTime2)/1000 - (endTime - startTime)/1000));
//       System.out.println("Batch is " + delta + " seconds faster");
       try {
           HashMap cveD = buildCVEDict(graph);
           HashMap xqD = buildXQDict();
           for (String cve: (Set<String>) cveD.keySet()){
               ArrayList entry = (ArrayList) cveD.get(cve);
               System.out.println(cve + ":  " + entry.size());
//               for (VulnResult r: (ArrayList<VulnResult>)entry){
//                   if (cve.equals("CVE-2017-14494")) {
//                       System.out.println(r.getTags());
//                   }
//               }
           }
           mergeCVE(graph,appearanceModel,appearanceController,cveD,xqD);


       } catch (SQLException e) {
           e.printStackTrace();
       }


//       LayoutController lc = Lookup.getDefault().lookup(LayoutController.class);
//       ForceAtlas fAL = new ForceAtlas();
//       lc.setLayout((Layout)fAL);
//       lc.executeLayout(5);

       AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
       autoLayout.setGraphModel(graphModel);
       ForceAtlasLayout firstLayout = new ForceAtlasLayout(null);
       AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.5f);//True after 10% of layout time
       AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 5000., 0.8f);
       autoLayout.addLayout(firstLayout, 1f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
       autoLayout.execute();


       PreviewModel previewModel = Lookup.getDefault().lookup(PreviewController.class).getModel();
       previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
       previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, FALSE);
       previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_COLOR, new DependantColor(Color.WHITE));
       previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, true);
       previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_BOX_OPACITY, 70);
       previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
       previewModel.getProperties().putValue(PreviewProperty.MARGIN, 10);


//       previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.LIGHT_GRAY));
//       previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGES, Boolean.FALSE);
//       previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
//       previewModel.getProperties().putValue(PreviewProperty.EDGE_LABEL_OUTLINE_COLOR, new DependantColor(Color.WHITE));
//       previewModel.getProperties().putValue(PreviewProperty.EDGE_LABEL_OUTLINE_OPACITY, 70)


//       GraphDistance dist = new GraphDistance();
//       dist.setDirected(true);
//       dist.execute(graph);
//       Column col = graph.getModel().getNodeTable().getColumn(GraphDistance.BETWEENNESS);
//
//
//       for (Node n : graph.getNodes()){
//           Double centrality = (Double) n.getAttributes()[col.getIndex()];
//       }
//Export
       ExportController ec = Lookup.getDefault().lookup(ExportController.class);
       try {
           ec.exportFile(new File("gephi"+ fileName.split("\\.")[0]+".pdf"));
       } catch (IOException ex) {
           ex.printStackTrace();
           return;
       }}

    public static Connection getConnection() throws SQLException,
            java.lang.ClassNotFoundException, IOException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String userName = "root";
        String password = "root";
        String url="jdbc:mysql://xqdb:3306/xq_vulns?autoReconnect=true&useSSL=false";
        Connection con = DriverManager.getConnection(url, userName, password);
        return con;
    }

    // RETURNING FEWER UNIQUE CVES, WHY????
    public static void searchDbInd(Graph graph){

        Pattern pattern = Pattern.compile("CVE-[0-9-]*");
        try{
            Connection con = getConnection();
            Statement sql = con.createStatement();
            int count = 0;
        for (Node n: graph.getNodes()) {
            String label = n.getLabel();
            Matcher matcher = pattern.matcher(label);
            if (matcher.find()) {
                String query = "select * from vulnerabilities_details JOIN vulnerabilities ON vulnerabilities.uuid = vulnerabilities_details.vulnerability_uuid where source_id=2 and script_tags LIKE '%" + matcher.group() + "%';";
                //query = "select * from vulnerabilities_details JOIN vulnerabilities ON vulnerabilities.uuid = vulnerabilities_details.vulnerability_uuid where source_id=2 and script_tags LIKE '%CVE-2017-9078%';";

                //System.out.println(query);
                ResultSet result = sql.executeQuery(query);
//                result.last();
//                System.out.println(result.getRow());
                if (result.next()) {
                    count++;
//                String res = result.getString("name");
                    System.out.println(result.getString("name"));
                    while (result.next()) {
                        count++;
                        System.out.println(result.getString("name"));
//                    res = res + ", " + result.getString("name");
                    }
//                System.out.println(res);
                } else {
                    System.out.println("no xq link");
                }

            }
        }
            System.out.println(count);
        }
                catch (SQLException ex) {
                    System.err.println("SQLException:" + ex.getMessage());
                } catch (ClassNotFoundException e) {


                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }



       public static ResultSet searchDb(Graph graph){
        boolean firstSearch = TRUE;
        Pattern pattern = Pattern.compile("CVE-[0-9-]*");
        String query = "select * from vulnerabilities_details JOIN vulnerabilities ON vulnerabilities.uuid = vulnerabilities_details.vulnerability_uuid where source_id=2";
        for (Node n: graph.getNodes()) {
            String label = n.getLabel();
            Matcher matcher = pattern.matcher(label);
            if (matcher.find()) {
                if(firstSearch){
                    query = query + " and script_tags regexp '.*cve_id[-\":, CVE0-9]*" + matcher.group() + "[,\"]'";
                    firstSearch = FALSE;
                }
                else {
                    //query = query + " or script_tags like '%" + matcher.group() + "%'";
                    //query = query + " or script_tags like '%cve_id' '%" + matcher.group() + "%'";
                    query = query + " or script_tags regexp '.*cve_id[-\":, CVE0-9]*" + matcher.group() + "[,\"]'";
                }
            }
        }
        query = query + ";";
        System.out.println(query);
        if (!firstSearch){
        try{
            Connection con = getConnection();
            Statement sql = con.createStatement();

            //System.out.println(query);
            ResultSet result = sql.executeQuery(query);
            return result;
//            if (result.next()){
//                int count = 1;
////                String res = result.getString("name");
//                System.out.println(result.getString("name"));
//                while (result.next()){
////                    res = res + ", " + result.getString("name");
//                    System.out.println(result.getString("name"));
//                    count++;
//                }
////                System.out.println(res);
//                System.out.println(count);
//            }
//            else{
//                System.out.println("no xq links at all");
//            }

        }
        catch (SQLException ex) {
            System.err.println("SQLException:" + ex.getMessage());
        } catch (ClassNotFoundException e) {


            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }}
        return null;


    }

    public static HashMap buildXQDict() throws SQLException{
           String query = "select * from xqvulnerabilities;";
            try {
                Connection con = getConnection();
                Statement sql = con.createStatement();
                ResultSet result = sql.executeQuery(query);
                HashMap<String, XQResult> xqD = new HashMap<String, XQResult>();
                while (result.next()){
                    xqD.put(result.getString("uuid"),new XQResult(result));
                }
                return xqD;

            }
            catch (SQLException ex) {
                System.err.println("SQLException:" + ex.getMessage());
            } catch (ClassNotFoundException e) {


                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
    }

    public static HashMap buildCVEDict(Graph graph) throws SQLException {
        ResultSet results = searchDb(graph);
        HashMap<String, ArrayList> cveDict = new HashMap<String, ArrayList>();
        Pattern pattern = Pattern.compile("CVE-[0-9-]*");

        try {
            while (results.next()){
                VulnResult res = new VulnResult(results);
                Matcher matcher = pattern.matcher(results.getString("script_tags"));
                while (matcher.find()){
                    String cve =  matcher.group();
                    if (cveDict.containsKey(cve)) {
                        ArrayList<VulnResult> compRes = cveDict.get(cve);
                        Boolean in = FALSE;
                        for (VulnResult r : compRes) {
                            if (r.getId() == res.getId()){// && r.getXq_uuid().equals(res.getXq_uuid())) {
                                in = TRUE;
                            }
                        }
                        if (!in) {
                            cveDict.get(cve).add(res);
                        }
                    }
                    else {
                        ArrayList<VulnResult> vulnResult = new ArrayList<VulnResult>();
                        vulnResult.add(res);
                        cveDict.put(cve, vulnResult);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cveDict;
    }

    public static void mergeCVE(Graph graph, AppearanceModel appearanceModel, AppearanceController appearanceController, HashMap cveD, HashMap xqD){
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Pattern cvepattern = Pattern.compile("CVE-[0-9-]*");
        HashMap<String, ArrayList<Node>> nodeMap = new HashMap<String, ArrayList<Node>>();
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);

        for (Node n: graph.getNodes().toArray()){
            Matcher matcher = pattern.matcher(n.getLabel());
            if (matcher.find()) {
                String ipOne = matcher.group();
                if (matcher.find()){
                    String ipTwo = matcher.group();
                    n.setLabel("");
                    gec.deleteNode(n);

//DO SOMETHING WITH HACL NODES
                }
                else {
                    if (nodeMap.containsKey(ipOne)) {
                        nodeMap.get(ipOne).add(n);
                    } else {
                        ArrayList<Node> na = new ArrayList<Node>();
                        na.add(n);
                        nodeMap.put(ipOne, na);
                    }
                }
            } else{
                System.out.println("no IP found: " + n.getLabel());
                n.setLabel("EXTERNAL ATTACKER");
                n.setColor(Color.RED);
            }
        }
        Column vulnC = graph.getModel().getNodeTable().addColumn("Vulns", "Vulnerability Count", Integer.class, 0);
        Column[] columns =  nodeMap.get(nodeMap.keySet().toArray()[0]).get(0).getAttributeColumns().toArray();
        AttributeRowsMergeStrategy[] strats = new AttributeRowsMergeStrategy[columns.length];
        for (String ip: nodeMap.keySet()){
            //Id, Label, Interval, shape
//            System.out.println(nodes.get(0).getAttributeColumns().toArray()[2]);
            ArrayList<Node> nodes = nodeMap.get(ip);
            int size = nodes.size();
            Node[] nods = new Node[size];
            for (int n=0;n<nodes.size();n++) {
                nods[n] = nodes.get(n);
            }
            String[] labels = new String[nodes.size()];
            for (int n=0;n<nodes.size();n++) {
                labels[n] = nodes.get(n).getLabel();
            }

// DELETES OBJECTIVE NODES
            Node newN = gec.mergeNodes(graph,nods,null, columns, strats, true);
            newN.setLabel(ip);
            newN.setAttribute(vulnC,size);
            newN.setColor(Color.gray);
            Set<String> uuids = new HashSet<String>();
            for (String preNode : labels) {
                Matcher cvematcher = cvepattern.matcher(preNode);
                if (cvematcher.find()){
                    String cve = cvematcher.group();
                    if (cveD.containsKey(cve)){
                        ArrayList<VulnResult> cveResults = (ArrayList<VulnResult>) cveD.get(cve);
                        for (VulnResult cveResult : cveResults) {
                            if (xqD.containsKey(cveResult.getXq_uuid())){
                                XQResult xqR = (XQResult) xqD.get(cveResult.getXq_uuid());
                                uuids.add(xqR.getName());
                            }
                        }
                    }
                }

//                if (cvematcher.find()) {
//                    String cve = cvematcher.group();
//                    String label = "";
//                    if (cveD.containsKey(cve)) {
//                        for (VulnResult res : (ArrayList<VulnResult>) cveD.get(cve)) {
//                            if (res.getXq_severity() > 4) {
//                                label = cve;
//                            }
//                        }
//                    }
//                    Node cveNode = gec.createNode(label);
//                    Edge cveedge = gec.createEdge(cveNode, newN, TRUE);
//                    cveNode.setColor(Color.GREEN);
//                    cveNode.setAttribute(vulnC, 0);
//                }
            }
            for (String name : uuids){
                Node xqNode = gec.createNode(name);
                Edge xqEdge = gec.createEdge(xqNode, newN, TRUE);
                xqNode.setColor(Color.GREEN);
                xqNode.setAttribute(vulnC, 0);
            }

        }

        GraphDistance dist = new GraphDistance();
        dist.setDirected(true);
        dist.execute(graph);
        Function centralityRanking = appearanceModel.getNodeFunction(graph,vulnC,RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = centralityRanking.getTransformer();
        centralityTransformer.setMinSize(1);
        centralityTransformer.setMaxSize(10);
        appearanceController.transform(centralityRanking);
    }

    public static void mergeIntoIPs(Graph graph, AppearanceModel appearanceModel, AppearanceController appearanceController){
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        HashMap<String, ArrayList<Node>> nodeMap = new HashMap<String, ArrayList<Node>>();
        for (Node n: graph.getNodes()){
//            System.out.print(n.getLabel());
            Matcher matcher = pattern.matcher(n.getLabel());
            if (matcher.find()) {
//               System.out.println(matcher.group());
                if (nodeMap.containsKey(matcher.group())){
                    nodeMap.get(matcher.group()).add(n);
                }
                else{
                    ArrayList<Node> na = new ArrayList<Node>();
                    na.add(n);
                    nodeMap.put(matcher.group(), na);
                }
            } else{
                System.out.println("no IP found: " + n.getLabel());
                n.setLabel("EXTERNAL ATTACKER");
                n.setColor(Color.RED);
            }
        }
        Column vulnC = graph.getModel().getNodeTable().addColumn("Vulns", "Vulnerability Count", Integer.class, 0);
        GraphElementsController gec = Lookup.getDefault().lookup(GraphElementsController.class);
        Column[] columns =  nodeMap.get(nodeMap.keySet().toArray()[0]).get(0).getAttributeColumns().toArray();
        AttributeRowsMergeStrategy[] strats = new AttributeRowsMergeStrategy[columns.length];
        for (String ip: nodeMap.keySet()){
            //Id, Label, Interval, shape
//            System.out.println(nodes.get(0).getAttributeColumns().toArray()[2]);
            ArrayList<Node> nodes = nodeMap.get(ip);
            int size = nodes.size();
            Node[] nods = new Node[size];
            for (int n=0;n<nodes.size();n++){
                nods[n]=nodes.get(n);
            }
            Node newN = gec.mergeNodes(graph,nods,null, columns, strats, true);
            newN.setLabel(ip);
            newN.setAttribute(vulnC,size);
            newN.setColor(Color.gray);

        }

        GraphDistance dist = new GraphDistance();
        dist.setDirected(true);
        dist.execute(graph);
        Function centralityRanking = appearanceModel.getNodeFunction(graph,vulnC,RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = centralityRanking.getTransformer();
        centralityTransformer.setMinSize(1);
        centralityTransformer.setMaxSize(10);
        appearanceController.transform(centralityRanking);
    }

   }

   class XQResult {
       private int id;
       private String xq_uuid;
       private String name;
       private String parent_uuid;
       private String category_uuid;


       public XQResult(ResultSet rs) {
           try {
               id = rs.getInt(1);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               xq_uuid = rs.getString("uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               name = rs.getString("name");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               parent_uuid = rs.getString("parent_uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               category_uuid = rs.getString("xqvulnerabilities_category_uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }

       public int getId() {
           return id;
       }

       public String getXq_uuid() {
           return xq_uuid;
       }

       public String getName() {
           return name;
       }

       public String getParent_uuid() {
           return parent_uuid;
       }

       public String getCategory_uuid() {
           return category_uuid;
       }
   }

   class VulnResult {
       private int id;
       private String family;
       private Blob description;
       private double cvss_base;
       private String patch;
       private Blob solution;
       private String plugin_type;
       private String uuid;
       private String tags;
       private String created;
       private String updated;
       private int details_id;
       private String source;
       private String name;
       int severity;
       private int xq_severity;
       private int xq_expoitability;
       private int active;
       private String xq_uuid;
       private String category_uuid;
       private int critical;
       private String cvss_vector;

       public VulnResult(ResultSet rs){
           try {
               id = rs.getInt(1);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               family = rs.getString("family");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               description = rs.getBlob("description");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               cvss_base = rs.getDouble("cvss_base_score");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               patch = rs.getString("patch_date");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               solution = rs.getBlob("solution");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               plugin_type = rs.getString("plugin_type");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               uuid = rs.getString("vulnerability_uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               tags = rs.getString("script_tags");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               created = rs.getString("created_at");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               updated = rs.getString("updated_at");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               details_id = rs.getInt(13);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               source = rs.getString("source_ref");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               name = rs.getString("name");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               severity = rs.getInt("severity");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               xq_severity = rs.getInt("xqseverity");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               xq_expoitability = rs.getInt("xqexploitability");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               active = rs.getInt("active");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               xq_uuid = rs.getString("xqvulnerability_uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               category_uuid = rs.getString("vulnerabilities_category_uuid");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               critical = rs.getInt("critical");
           } catch (SQLException e) {
               e.printStackTrace();
           }
           try {
               cvss_vector = rs.getString("cvss_vector");
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }


       public int getId() {
           return id;
       }

       public String getFamily() {
           return family;
       }

       public Blob getDescription() {
           return description;
       }

       public double getCvss_base() {
           return cvss_base;
       }

       public String getPatch() {
           return patch;
       }

       public Blob getSolution() {
           return solution;
       }

       public String getPlugin_type() {
           return plugin_type;
       }

       public String getUuid() {
           return uuid;
       }

       public String getTags() {
           return tags;
       }

       public String getCreated() {
           return created;
       }

       public String getUpdated() {
           return updated;
       }

       public int getDetails_id() {
           return details_id;
       }

       public String getSource() {
           return source;
       }

       public String getName() {
           return name;
       }

       public int getSeverity() {
           return severity;
       }

       public int getXq_severity() {
           return xq_severity;
       }

       public int getXq_expoitability() {
           return xq_expoitability;
       }

       public int getActive() {
           return active;
       }

       public String getXq_uuid() {
           return xq_uuid;
       }

       public String getCategory_uuid() {
           return category_uuid;
       }

       public int getCritical() {
           return critical;
       }

       public String getCvss_vector() {
           return cvss_vector;
       }
   }
