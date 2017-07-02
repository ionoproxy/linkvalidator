import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
public class LinkValidator extends JFrame{


	static ArrayList<String> archivosAValidar = new ArrayList<String>();
	static Scanner input = new Scanner(System.in);
	static JProgressBar pgrStatus;
	
	
	public LinkValidator() throws InterruptedException{
		super("LinkValidator");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		cambiarAparienciaVentana();
		
		pgrStatus = new JProgressBar();
		pgrStatus.setPreferredSize(new Dimension(400,40));
		pgrStatus.setMinimum(0);
		pgrStatus.setMaximum(100);
		pgrStatus.setStringPainted(true);
		
		add(pgrStatus);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
		final String DIRECTORIO_PREDETERMINADO = System.getProperty("user.home");
		final int SOLAMENTE_CARPETAS = JFileChooser.DIRECTORIES_ONLY;
		final int CANCELAR = JFileChooser.CANCEL_OPTION;
		final Component CENTRADO = null;
		
		JFileChooser seleccionDeCarpeta = new JFileChooser(DIRECTORIO_PREDETERMINADO);
		seleccionDeCarpeta.setFileSelectionMode(SOLAMENTE_CARPETAS);
		seleccionDeCarpeta.setApproveButtonText("Seleccionar carpeta");
		
		int accionRealizada = seleccionDeCarpeta.showOpenDialog(CENTRADO);
		
		if(accionRealizada != CANCELAR){
		
		final String carpetaSeleccionada = seleccionDeCarpeta.getSelectedFile().getAbsolutePath();
		final boolean ES_UNA_CARPETA_PRINCIPAL = true;
	
		encontrarArchivosIndexEn(carpetaSeleccionada, ES_UNA_CARPETA_PRINCIPAL);
		analizarArchivos();
		
		pgrStatus.setString("Finalizado");
		
		int preguntaRepetirOperacion = JOptionPane.showConfirmDialog(null, "Se ha terminado de validar todos los archivos.\n\n¿Seleccionar otra carpeta?", "Finalizado", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
		final int RESPUESTA_SI = JOptionPane.YES_OPTION;
		
		if(preguntaRepetirOperacion == RESPUESTA_SI){
			abrirNuevaVentana();
			cerrarVentanaActual();
		}else{
			cerrarVentanaActual();
		}
		
		}else{
			cerrarVentanaActual();
		}
	}


	private void cambiarAparienciaVentana() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}


	private void abrirNuevaVentana() throws InterruptedException {
		new LinkValidator();
	}


	private void cerrarVentanaActual() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	
	public static void encontrarArchivosIndexEn(String rutaSeleccionada, boolean carpetaPrincipal) throws InterruptedException{

		if(carpetaPrincipal){
			pgrStatus.setString("Buscando archivos index.html...");
		}
		
		File rutaAbsoluta = new File(rutaSeleccionada);
		File archivoIndex = new File(rutaSeleccionada + "\\index.html");
		File archivoForm = new File(rutaSeleccionada + "\\form.html");
		
		final boolean ARCHIVO_A_ENCONTRAR = archivoIndex.exists();
		final boolean ARCHIVO_TIENE_NOMBRE_INCORRECTO = archivoForm.exists();
		final boolean ENCONTRADO = true;
		final boolean NO_ENCONTRADO = false;
		
		final String RUTA_DE_ARCHIVO_INDEX = archivoIndex.getAbsolutePath();
		
		if(ARCHIVO_TIENE_NOMBRE_INCORRECTO){
		
			preguntaCambiarNombre(rutaSeleccionada, archivoIndex, archivoForm, RUTA_DE_ARCHIVO_INDEX);
			
		}else if(ARCHIVO_A_ENCONTRAR == NO_ENCONTRADO){
			
			encontrarSubcarpetasEn(rutaSeleccionada, rutaAbsoluta);
					
		}else if(ARCHIVO_A_ENCONTRAR == ENCONTRADO){
			archivosAValidar.add(RUTA_DE_ARCHIVO_INDEX);
			pgrStatus.setMaximum(archivosAValidar.size() + 1);
			pgrStatus.setValue(archivosAValidar.size());
			Thread.sleep(100);
		}
		
		
		if(carpetaPrincipal){
			pgrStatus.setValue(archivosAValidar.size() + 1);
		}

	}


	private static void preguntaCambiarNombre(String rutaSeleccionada, File archivoIndex, File archivoForm,
			final String RUTA_DE_ARCHIVO_INDEX) {
		int preguntaCambiarNombre = JOptionPane.showConfirmDialog(null, "Se ha encontrado un archivo form.html en la ruta " + (rutaSeleccionada) +"\n\n ¿Cambiar nombre a index.html?", "Archivo form.html detectado", JOptionPane.YES_NO_OPTION);
		final int RESPUESTA_SI = JOptionPane.YES_OPTION;
		
		if(preguntaCambiarNombre == RESPUESTA_SI){
			archivoForm.renameTo(archivoIndex);
			archivosAValidar.add(RUTA_DE_ARCHIVO_INDEX);
			pgrStatus.setMaximum(archivosAValidar.size() + 1);
			pgrStatus.setValue(archivosAValidar.size());
		}
	}


	private static void encontrarSubcarpetasEn(String rutaSeleccionada, File rutaAbsoluta) throws InterruptedException {
		
		String[] subcarpetasEnLaCarpeta = rutaAbsoluta.list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				boolean esUnaCarpeta = new File(dir, name).isDirectory();
				return esUnaCarpeta;
			}
		});
		
		final int NUMERO_DE_SUBCARPETAS = subcarpetasEnLaCarpeta.length;
		final int NINGUNA = 0;
		
		if(NUMERO_DE_SUBCARPETAS != NINGUNA){
			for (int i = 0; i < NUMERO_DE_SUBCARPETAS; i++) {
				final String SUBCARPETA = subcarpetasEnLaCarpeta[i];
				final boolean NO_ES_UNA_CARPETA_PRINCIPAL = false;
				encontrarArchivosIndexEn(rutaSeleccionada + "\\" + SUBCARPETA, NO_ES_UNA_CARPETA_PRINCIPAL);
			}
		}else{
			return;
		}
	}
	
	public static void analizarArchivos() throws InterruptedException{
		
		pgrStatus.setString("Validando los archivos encontrados...");
		pgrStatus.setValue(0);
		pgrStatus.setMaximum(archivosAValidar.size());
		int i = 0;
		for (String archivo : archivosAValidar) {
			i++;
			pgrStatus.setValue(i);
			Thread.sleep(100);
			try {
				FileReader lectorDeArchivo = new FileReader(archivo);
				BufferedReader bufferDeLectorDeArchivo = new BufferedReader(lectorDeArchivo);
				String archivoModificado = "";
				
				boolean finDeArchivo = false;
				
				while(!finDeArchivo){
					String lineaDeArchivo = bufferDeLectorDeArchivo.readLine();
					final String LINEA_FINAL = null;
					
					if(lineaDeArchivo == LINEA_FINAL){
						finDeArchivo = true;
						break;
					}
					
					int codigoABuscar = lineaDeArchivo.indexOf("<a ");
					final int NO_ENCONTRADO = -1;
					
					if(codigoABuscar != NO_ENCONTRADO){
						int href = lineaDeArchivo.indexOf("href=\"");
						System.out.println("Se ha encontrado un elemento href.\n\n" + lineaDeArchivo);
						String lineaSinHref = lineaDeArchivo.substring(href + 6, lineaDeArchivo.indexOf(">"));
						int posComillaDeCierre = lineaSinHref.indexOf("\"");
						String linkConArchivo = lineaSinHref.substring(0, posComillaDeCierre);
						System.out.println("El link identificado con archivo es: " + linkConArchivo);
						System.out.println();
						
						File probarRuta = new File(linkConArchivo);
						boolean rutaCorrecta = probarRuta.exists();
						if(rutaCorrecta){
							System.out.println("La ruta del archivo coincide con la de la carpeta.");
						}else{
							
							while(true){
								
								String rutaCorrejida = JOptionPane.showInputDialog("Se ha encontrado un error de ruta en el archivo: " + archivo + "\nRuta erronea: " + linkConArchivo + "\n\nCarpetas dentro de la carpeta padre:\n" + mostrarCarpetasDe(archivo) + "\nIntroduzca la ruta correctamente:", linkConArchivo);
								
								if(rutaCorrejida != null && new File(rutaCorrejida).exists()){
								lineaDeArchivo = lineaDeArchivo.replace(linkConArchivo, rutaCorrejida);
								break;
								}else{
									JOptionPane.showMessageDialog(null, "La ruta especificada no existe, intente otra.", "La ruta no existe", JOptionPane.WARNING_MESSAGE);
								}
							}
						}
						
					
					}
					
					archivoModificado += lineaDeArchivo + "\n";
					
					
				}
				

				FileWriter flIndexWriter = new FileWriter(archivo);
				BufferedWriter bfIndexWriter = new BufferedWriter(flIndexWriter);
				
				bfIndexWriter.write(archivoModificado);
				
				bfIndexWriter.close();
				flIndexWriter.close();
				
				bufferDeLectorDeArchivo.close();
				lectorDeArchivo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private static String mostrarCarpetasDe(String archivo) {
		String rutaArchivo = archivo.substring(0, archivo.lastIndexOf("\\"));
		System.out.println(rutaArchivo);
		String rutaCarpetaPadre = rutaArchivo.substring(0, rutaArchivo.lastIndexOf("\\"));
		System.out.println(rutaCarpetaPadre);
		String[] carpetas = new File(rutaCarpetaPadre).list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
			
		});
		
		String nombresDeCarpetas = "";
		
		for(String nombreCarpeta : carpetas){
			nombresDeCarpetas += nombreCarpeta + "\n";
		}
		
		return nombresDeCarpetas;
	}
	
	public static void main(String[] args) throws InterruptedException {
	
		new LinkValidator();
		
	}
	

}
