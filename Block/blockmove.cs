using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Linq;
public class blockmove : MonoBehaviour {
    private Rigidbody rb;
    public Text text;
    public static int len = Block.blockmove.Count;
    // Use this for initialization
    void Start () {
        
	}
	
	// Update is called once per frame
	void Update () {
		
	}
    public void print()
    {
        string print = "";
        foreach (List<string> s in Block.a)
        {
            foreach (string k in s)
            {
                print += " " + k;
            }
            print += "改行";
        }
        Debug.Log(print);
    }
    public void delete(string delete)
    {
        int count = 0;
        List<List<string>> a1 = new List<List<string>>();
        foreach (List<string> s in Block.a)
        {
            List<string> x = new List<string>();
            foreach (string k in s)
            {
                
                if (k.Equals(delete))
                {
                    Block.blocksize[count]--;         
                }
                else
                {
                    x.Add(k);
                }
            }        
            a1.Add(x);
            count++;
        }
        Block.a = a1;
    }
    public void add(string add,string hold)
    {
        List<List<string>> a1 = new List<List<string>>();
        int count = 0;
        foreach (List<string> s in Block.a)
        {
            List<string> x = new List<string>();
            foreach (string k in s)
            {
                if (k.Equals(add))
                {
                    x.Add(k);
                    x.Add(hold);
                }
                else
                {
                    x.Add(k);
                }
                
            }
            a1.Add(x);
            count++;
        }
        Block.a = a1;
        count = 0;
        foreach (List<string> s in Block.a)
        {
            foreach (string k in s)
            {
                if (k.Equals(add))
                {
                    Block.blocksize[count]++;
                }
            }
 
           // Debug.Log("追加後" + count + "列目" + Block.blocksize[count]);
            count++;
        }
    }
    public void ButtonPush()
    {
        len = Block.blockmove.Count;
        StartCoroutine("stop");
    }
    public int number(string x)
    {
        int y=0;
        int count = 0;
        foreach (string s in Block.blockname)
        {
            if (s.Equals(x))
            {
                y = count;
            }
            count++;
        }
        return y;
    }
    IEnumerator stop()
    {

        int counter = 0;
        if (Block.mode == 1 || Block.mode == 2)
        {

            string x = Block.hold;
            //Debug.Log(x);
            int y = -1;
            int y1 = -1;
            string hold = x;
            if (Block.map.TryGetValue(hold, out y1))
            {

            }
            delete(hold);
            Block.map.Remove(hold);
            // Debug.Log("移動位置"+y1);
            int before = y1;
            y = number(hold);
            rb = Block.blockList[y].GetComponent<Rigidbody>();
            x = Block.blockmove[counter];
            counter++;
            string find = x;
            if (Block.map.TryGetValue(find, out y1))
            {
            }
            else
            {
                int where = 0;
                foreach (int i in Block.blocksize)
                {
                    if (i == 0)
                    {
                        y1 = -2 + where * 2;
                        Block.blocksize[where]++;
                        break;
                    }
                    else
                    {
                        where++;
                    }
                }
            }
            add(find, hold);
            //Debug.Log("移動後" + y1);
            Block.map.Add(hold, y1);
            int after = y1;
            int move = after - before;
            if (move < 0)
            {
                move = move * -1;
                rb.velocity = new Vector3(-1, 0, 0);
            }
            else
            {
                rb.velocity = new Vector3(1, 0, 0);
            }
            yield return new WaitForSeconds(move);
            rb.velocity = new Vector3(0, 0, 0);
            rb.useGravity = true;
            yield return new WaitForSeconds(2);
            len--;
        }
        while (counter < len-2)
        {
            string x = Block.blockmove[counter];
            //Debug.Log(x);
            counter++;
            int y = -1;
            int y1 = -1;
            string hold = x;
            if (Block.map.TryGetValue(hold, out y1))
            {

            }     
            delete(hold);           
            Block.map.Remove(hold);
           // Debug.Log("移動位置"+y1);
            int before = y1;
            y = number(hold);
            rb = Block.blockList[y].GetComponent<Rigidbody>();
            rb.useGravity = false;
            rb.velocity = new Vector3(0, 10, 0);
            yield return new WaitForSeconds(1);
            x = Block.blockmove[counter];
            counter++;
            string find = x;
            if (Block.map.TryGetValue(find, out y1))
            {
            }
            else
            {
                    int where = 0;
                foreach(int i in Block.blocksize)
                {
                    if (i == 0)
                    {
                        y1 = -2 + where * 2;
                        Block.blocksize[where]++;
                        break;
                    }
                    else
                    {
                        where ++;
                    }
                }
            }
            add(find,hold);
            //Debug.Log("移動後" + y1);
            Block.map.Add(hold, y1);
            int after = y1;
            int move = after - before;
            if (move < 0)
            {
                move = move * -1;
                rb.velocity = new Vector3(-1, 0, 0);
            }
            else
            {
                rb.velocity = new Vector3(1, 0, 0);
            }
            yield return new WaitForSeconds(move);
            rb.velocity = new Vector3(0, 0, 0);
            rb.useGravity = true;
            yield return new WaitForSeconds(2);

        }
        if (Block.mode == 4||Block.mode==2)
        {
            string x = Block.blockmove[counter];
            //Debug.Log(x);
            counter++;
            int y = -1;
            int y1 = -1;
            string hold = x;
            if (Block.map.TryGetValue(hold, out y1))
            {

            }
            delete(hold);
            Block.map.Remove(hold);
            // Debug.Log("移動位置"+y1);
            int before = y1;
            y = number(hold);
            rb = Block.blockList[y].GetComponent<Rigidbody>();
            rb.useGravity = false;
            rb.velocity = new Vector3(0, 10, 0);
            yield return new WaitForSeconds(1);
            x = Block.blockmove[counter];
            counter++;
            string find = x;
            if (Block.map.TryGetValue(find, out y1))
            {
            }
            else
            {
                int where = 0;
                foreach (int i in Block.blocksize)
                {
                    if (i == 0)
                    {
                        y1 = -2 + where * 2;
                        break;
                    }
                    else
                    {
                        where++;
                    }
                }
            }
            add(find, hold);
            //Debug.Log("移動後" + y1);
            Block.map.Add(hold, y1);
            int after = y1;
            int move = after - before;
            if (move < 0)
            {
                move = move * -1;
                rb.velocity = new Vector3(-1, 0, 0);
            }
            else
            {
                rb.velocity = new Vector3(1, 0, 0);
            }
            yield return new WaitForSeconds(move);
            rb.velocity = new Vector3(0, 0, 0);
            rb.useGravity = true;
            yield return new WaitForSeconds(2);
        }
        else if (Block.mode == 3||Block.mode==1)
        {
            string x = Block.blockmove[counter];
            //Debug.Log(x);
            counter++;
            int y = -1;
            int y1 = -1;
            string hold = x;
            if (Block.map.TryGetValue(hold, out y1))
            {

            }
            delete(hold);
            Block.map.Remove(hold);
            // Debug.Log("移動位置"+y1);
            int before = y1;
            y = number(hold);
            rb = Block.blockList[y].GetComponent<Rigidbody>();
            rb.useGravity = false;
            rb.velocity = new Vector3(0, 10, 0);
            yield return new WaitForSeconds(1);
            rb.velocity = new Vector3(0, 0, 0);
        }
        Debug.Log("終了");
        text.enabled = true;
    }
    

}
