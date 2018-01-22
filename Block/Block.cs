using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Linq;
using System.Collections.Generic;
using System.IO; //System.IO.FileInfo, System.IO.StreamReader, System.IO.StreamWriter
using System; //Exception
using System.Text; //Encoding

public class Block : MonoBehaviour {
    public static List<List<string>> a = new List<List<string>>();//初期のブロック状態
    public static List<GameObject> blockList = new List<GameObject>();
    public static List<string> blockmove = new List<string>();//動かす順番
    public static List<string> blockname = new List<string>();
    public static List<int> blocksize = new List<int>();//ブロックの配置している数
    public static Dictionary<string ,int> map = new Dictionary<string, int>();
    public static int mode;
    public static string hold;
    private Rigidbody rb;
    // Use this for initialization
    public GameObject blockPrefab;
    public GameObject canvas;//キャンバス
    public Text text;
    private string guitxt = "";
    void Start()
    {
        text.enabled=false;
        move();
        blockread();
        for (int i = 0; i < blocksize.Count; i++)
        {
            Debug.Log(blocksize[i]);
        }
        Debug.Log("mode" + mode);
        Vector3 placePosition = new Vector3(-2, 10, 0);
        int x = -2;
        int y = 10;
        int y3 = 0 ;
        //配置する回転角を設定
        Quaternion q = new Quaternion();
        q = Quaternion.identity;
        foreach (List<string> s in a)
        {
            foreach (string k in s)
            {
                Debug.Log("k=" + k);
                //Instantiate(blockPrefab, placePosition, q);
                if (!k.Equals(" "))
                {
                    GameObject block = (GameObject)Instantiate(
                        blockPrefab,
                     new Vector3(x, y, 0),
                    Quaternion.identity
                        );
                    GameObject obj = (GameObject)Resources.Load("New Text");
                    obj.GetComponent<TextMesh>().text = k;
                    float x1 = x;
                    float y1 = y;
                    GameObject prefab = (GameObject)Instantiate(obj, new Vector3(x1 - 0.3f, y1 + 0.5f, 0.0f), Quaternion.identity);
                    // GameObject prefab = (GameObject)Instantiate(obj, new Vector3(x, y, 0), Quaternion.identity);
                    prefab.transform.SetParent(block.transform);
                    blockList.Add(block);
                    //Debug.Log(k);
                    string k1 = k.Substring(0, 1);
                    blockname.Add(k1);
                    map.Add(k1,x);
                    y += 2;
                    if (x == 0)
                        y3 = y;
                }
            }
            //x座標を変更し配置
            y = 10;
            x += 2;
        }
        if (mode == 1 || mode == 2)
        {
        
            GameObject block1 = (GameObject)Instantiate(
                        blockPrefab,
                     new Vector3(0, y3, 0),
                    Quaternion.identity
                        );
            GameObject obj1 = (GameObject)Resources.Load("New Text");
            obj1.GetComponent<TextMesh>().text = hold;
            float x2 = 0;
            float y2 = y3;
            GameObject prefab1 = (GameObject)Instantiate(obj1, new Vector3(x2 - 0.3f, y2 + 0.5f, 0.0f), Quaternion.identity);
            // GameObject prefab = (GameObject)Instantiate(obj, new Vector3(x, y, 0), Quaternion.identity);
            prefab1.transform.SetParent(block1.transform);
            rb = block1.GetComponent<Rigidbody>();
            rb.useGravity = false;
            blockList.Add(block1);
            //Debug.Log(k);
            string k2 = hold.Substring(0, 1);
            blockname.Add(k2);
            map.Add(k2, 0);
        }
    }
	
	// Update is called once per frame
	void Update () {

    }
    public void blockread()
    {
        ReadFile();

    }
    void move()
    {
        // FileReadTest.txtファイルを読み込む
        FileInfo fi = new FileInfo(Application.dataPath + "/../" + "move.txt");
        try
        {

            // 一行毎読み込み
            using (StreamReader sr = new StreamReader(fi.OpenRead(), Encoding.UTF8))
            {
                guitxt = sr.ReadToEnd();
                char[] separator = new char[] { '\n' };
                string[] splitted = guitxt.Split(separator, StringSplitOptions.RemoveEmptyEntries);
                int count = 0;
                foreach (string s in splitted)
                {
                    if (count == 0)
                    {                      
                        mode = Int32.Parse(s);
                   
                    }
                    else
                    {
                        string k1 = s.Substring(0, 1);
                        blockmove.Add(k1);
                    }
                    count++;
                }
            }
        }
        catch (Exception e)
        {
            // 改行コード
            Debug.Log("読み込めてない");
        }
    }
    void ReadFile()
    {
        // FileReadTest.txtファイルを読み込む
        FileInfo fi = new FileInfo(Application.dataPath + "/../" + "blockstate.txt");
        try
        {

            // 一行毎読み込み
            using (StreamReader sr = new StreamReader(fi.OpenRead(), Encoding.UTF8))
            {
                int count = 0;//ブロックの数
                int count1 = 0;//ブロックの山の高さ
                guitxt = sr.ReadToEnd();
                char[] separator = new char[] { '\n' };
                string[] splitted = guitxt.Split(separator, StringSplitOptions.RemoveEmptyEntries);
                int counter = 0;
                foreach (string s in splitted)
                {

                    if (counter == 0&&(mode==1||mode==2))
                    {
                        hold = s.Substring(0, 1);
                        counter++;
                    }
                    else
                    {
                        counter++;
                        List<string> b = new List<string>();
                        int foundIndex = s.IndexOf(" ");
                        //Debug.Log(s + foundIndex);
                        if (foundIndex < 0)
                        {
                            string k1 = s.Substring(0, 1);
                            b.Add(k1);
                            Debug.Log(k1);
                            a.Add(b);
                            count++;
                            blocksize.Add(1);
                        }
                        else
                        {

                            count1 = 0;
                            char[] separator1 = new char[] { ' ' };
                            string[] splitted1 = s.Split(separator1, StringSplitOptions.RemoveEmptyEntries);
                            foreach (string s1 in splitted1)
                            {
                                //Debug.Log(s1);
                                count++;
                                string k1 = s1.Substring(0, 1);
                                b.Add(k1);
                                count1++;
                            }
                            blocksize.Add(count1);
                            a.Add(b);
                        }
                    }
                }
                int len = count - blocksize.Count();
                for (int i = 0; i <= len; i++)
                {
                    blocksize.Add(0);
                }
            }
        }
        catch (Exception e)
        {
            // 改行コード
            Debug.Log("読み込めてない");
        }
    }
    string SetDefaultText()
    {
        return "C#あ\n";
    }

}
