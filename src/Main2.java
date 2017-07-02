import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
public class Main2 extends JFrame{


	static Scanner input = new Scanner(System.in);
	
	public Main2(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFileChooser flChoose = new JFileChooser(System.getProperty("user.home"));
		flChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		flChoose.setApproveButtonText("Seleccionar carpeta");
		int result = flChoose.showOpenDialog(null);
		
		if(result != JFileChooser.CANCEL_OPTION){
			
		
		String route = flChoose.getSelectedFile().getAbsolutePath();
		File routeDirectory = new File(route);
		
		String[] carpetas = routeDirectory.list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
			
			
		});
		
		comprobarCarpetas(route, carpetas, "");
		
		
		
		int repetirOperacion = JOptionPane.showConfirmDialog(null, "Se ha terminado de validar todos los archivos.\n\n¿Seleccionar otra carpeta?", "Finalizado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null);

		if(repetirOperacion == JOptionPane.YES_OPTION){
			new Main2();
		}else{
			try {
				this.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		input.close();
		}else{
			System.out.println("No se ha seleccionado ninguna carpeta.");
		}
		
	}
	
	public static void detectarLink(File archivo, String[] carpetas){
		
		
		try {
			FileReader flIndex = new FileReader(archivo);
			BufferedReader bfIndex = new BufferedReader(flIndex);
			String resultFile = "";
			
			boolean eof = false;
			while(!eof){
				String linea = bfIndex.readLine();
				final String FIN_DE_ARCHIVO = null;
				
				if(linea == FIN_DE_ARCHIVO){
					eof = true;
					break;
				}
				
				
				int codigoABuscar = linea.indexOf("href=\"");
				final int NO_ENCONTRADO = -1;
				
				if(codigoABuscar != NO_ENCONTRADO){
					System.out.println("Se ha encontrado un elemento href en la ruta: " + archivo.toString() + "\n\n" + linea);
					String lineaSinHref = linea.substring(codigoABuscar + 6, linea.indexOf(">"));
					int posComillaDeCierre = lineaSinHref.indexOf("\"");
					String linkConArchivo = lineaSinHref.substring(0, posComillaDeCierre);
					System.out.println("El link identificado con archivo es: " + linkConArchivo);
					System.out.println();
					
					File probarRuta = new File(linkConArchivo);
					boolean rutaCorrecta = probarRuta.exists();
					if(rutaCorrecta){
						System.out.println("La ruta del archivo coincide con la de la carpeta.");
					}else{
//						System.out.println("Se ha encontrado un error en la ruta.");
//						System.out.println("Ruta raíz: " + archivo.toString());
//						System.out.println("Ruta erronea: " + linkConArchivo);
//						System.out.println("Introduzca la ruta correctamente: ");
						while(true){
							String rutaCorrejida = JOptionPane.showInputDialog("Se ha encontrado un error en la ruta.\n\nRuta raíz: " + archivo.toString() + "\nRuta erronea: " + linkConArchivo + "\n\nCarpetas dentro de la ruta raíz:\n"+ Arrays.toString(carpetas) +"\n\nIntroduzca la ruta correctamente:", linkConArchivo);
							
							if(rutaCorrejida != null && new File(rutaCorrejida).exists()){
							linea = linea.replace(linkConArchivo, rutaCorrejida);
							break;
							}else if(rutaCorrejida == null){
								System.out.println("La ruta es null.");
								break;
							}else{
								JOptionPane.showMessageDialog(null, "La ruta especificada no existe, intente otra.", "La ruta no existe", JOptionPane.WARNING_MESSAGE);
							}
						}
					}
					
				
				}
				
				resultFile += linea + "\n";
				
				
			}
			

			FileWriter flIndexWriter = new FileWriter(archivo);
			BufferedWriter bfIndexWriter = new BufferedWriter(flIndexWriter);
			
			bfIndexWriter.write(resultFile);
			
			bfIndexWriter.close();
			flIndexWriter.close();
			
			bfIndex.close();
			flIndex.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String comprobarCarpetas(String route, String[] carpetas, String subcarpeta){
		for (int i = 0; i < carpetas.length; i++) {
			String[] carpetasSubcarpeta = null;
			File archivoIndex;
			File archivoForm;
			if(subcarpeta == ""){
				archivoIndex = new File(route + "\\" + carpetas[i] + "\\index.html");
				archivoForm = new File(route + "\\" + carpetas[i] + "\\form.html");
				System.out.println("He entrado en la ruta: " + archivoIndex.toString());
			}else{
				File subcarpetaRouteDirectory = new File(route);
				carpetasSubcarpeta = subcarpetaRouteDirectory.list(new FilenameFilter(){

					@Override
					public boolean accept(File dir, String name) {
						return new File(dir, name).isDirectory();
					}
					
					
				});
				
				System.out.println("He entrado en la ruta de la subcarpeta: " + subcarpetaRouteDirectory.toString() + "\n\n que contiene las subcarpetas:\n" + Arrays.toString(carpetasSubcarpeta));
				
				archivoIndex = new File(route + "\\index.html");
				archivoForm = new File(route + "\\" + "\\form.html");
			}
			boolean indexEncontrado = archivoIndex.exists();
			boolean formEncontrado = archivoForm.exists();
			if(indexEncontrado){
				System.out.println("El archivo index.html ha sido encontrado.");
				System.out.println("A continuación se procederá a la comprobación...");
				System.out.println();
				System.out.println();
				if(carpetasSubcarpeta.length != 0){
					detectarLink(archivoIndex, carpetasSubcarpeta);
				}else{
					detectarLink(archivoIndex, carpetas);
				}
			}else if(formEncontrado){
				int option = JOptionPane.showConfirmDialog(null, "Se ha encontrado un archivo form.html en la ruta " + (route + "\\" + carpetas[i]) +"\n\n ¿Cambiar nombre a index.html?", "Archivo form.html detectado", JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION){
					archivoForm.renameTo(archivoIndex);
					if(carpetasSubcarpeta.length != 0){
						detectarLink(archivoIndex, carpetasSubcarpeta);
					}else{
						detectarLink(archivoIndex, carpetas);
					}
				}else{
					continue;
				}
			}else{
				if(subcarpeta == ""){
					System.out.println("Todavía no hay ninguna subcarpeta.");
					return comprobarCarpetas(route + "\\" + carpetas[i], carpetas, carpetas[i]);
				}else{
					System.out.println("Ya hay subcarpeta: " + subcarpeta);
					if(carpetasSubcarpeta.length != 0){
						return comprobarCarpetas(route + "\\" + carpetasSubcarpeta[i], carpetasSubcarpeta, carpetasSubcarpeta[i]);
					}else{
						return "";
					}
				}
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
	
		new Main2();
		
	}
	

}
