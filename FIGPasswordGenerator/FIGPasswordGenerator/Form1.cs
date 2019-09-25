using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace WindowsFormsApplication1
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private string ByteArrayToString(byte[] ba)
        {
            StringBuilder hex = new StringBuilder(ba.Length * 2);
            foreach (byte b in ba)
                hex.AppendFormat("{0:x2}", b);
            return hex.ToString();
        }

        private void generateButtonClicked(object sender, EventArgs e)
        {
            byte[] key = null;
            byte[] snBytes = null;
            string b64String = this.base64Key.Text;
            string sn = this.serialNumber.Text;
            if (b64String.Length > 0 && sn.Length > 0)
            {
                byte[] b64 = Convert.FromBase64String(b64String);
                key = new byte[b64.Length - 4];
                Buffer.BlockCopy(b64, 4, key, 0, b64.Length - 4);
                snBytes = Encoding.ASCII.GetBytes(sn);

                byte[] data = new byte[snBytes.Length + key.Length];
                Buffer.BlockCopy(snBytes, 0, data, 0, snBytes.Length);
                Buffer.BlockCopy(key, 0, data, snBytes.Length, key.Length);

                System.Security.Cryptography.SHA1 sha = new System.Security.Cryptography.SHA1CryptoServiceProvider();
                byte[] fullResult = sha.ComputeHash(data);
                for (int i = 0; i < 31; i++)
                    fullResult = sha.ComputeHash(fullResult);

                string result = ByteArrayToString(fullResult).ToUpper();
                this.generatedPassword.Text = "Password: " + result.Substring(0, 8);
            }
        }

        private void closeButton_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
