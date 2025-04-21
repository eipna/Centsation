<div align="center">

<img width="" src="metadata/en-US/images/icon.png"  width=160 height=160  align="center">

# Centsation

Centsation is the easy-to-use savings tracker that helps you reach your financial goals! Whether you're saving for a vacation, a new gadget, or an emergency fund, Centsation makes it simple and straightforward.

</div>

## Features

- Free and open source
- Material 3 design with support for dynamic colors
- Support for UI contrasts (low, medium or high)
- Support for dark mode
- Support for multiple currencies
- Archived savings
- Export and import to and from JSON
- History of transactions
- Optional deadlines
- Sort by name, current saving, goal or deadline

## Screenshots

<div align="center">
	<div>
		<img src="metadata/en-US/images/Phone Screenshots/Screenshot 1.jpg" width="30%" />
    <img src="metadata/en-US/images/Phone Screenshots/Screenshot 2.jpg" width="30%" />
    <img src="metadata/en-US/images/Phone Screenshots/Screenshot 3.jpg" width="30%" />
    <img src="metadata/en-US/images/Phone Screenshots/Screenshot 4.jpg" width="30%" />
    <img src="metadata/en-US/images/Phone Screenshots/Screenshot 5.jpg" width="30%" />
    <img src="metadata/en-US/images/Phone Screenshots/Screenshot 6.jpg" width="30%" />
	</div>
</div>

## Verification

APK releases on GitHub are signed using my key. They can
be verified using
[apksigner](https://developer.android.com/studio/command-line/apksigner.html#options-verify):

```
apksigner verify --print-certs --verbose centsation.apk
```

The output should look like:

```
Verifies
Verified using v2 scheme (APK Signature Scheme v2): true
```

The certificate fingerprints should correspond to the ones listed below:

```
CN=Vrixzandro Eliponga
O=OSSentials
OU=Hobbyist Developer
Certificate fingerprints:
   MD5:  a8a82d68a60fe6ecf45eff4550b94d6f
   SHA1: af8be376426c6725fc3bdb287abeb268bf94b768
   SHA256: 561f3fec72e1f9878c2749d079f8b2175d02131c842955714e63365da5301baa
```

## License

This project is licensed under the GNU General Public License v3.0. See the
[LICENSE](LICENSE) file for details.
